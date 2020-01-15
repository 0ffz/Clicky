from django.db.models import F
from django.http import HttpResponseRedirect, HttpResponseForbidden, Http404, JsonResponse
from django.shortcuts import get_object_or_404, render
from django.urls import reverse
from django.utils import timezone
from django.utils.text import slugify
from django.views import generic

from .forms import ClickyCreateForm, ClickyJoinForm
from .models import Choice, Room


def validate_or_404(room_id, slug):
    room = get_object_or_404(Room, id=str(room_id).upper())
    if room.slug() == slugify(slug):
        return room
    raise Http404


class IndexView(generic.FormView, generic.TemplateView):
    template_name = 'clicky/index.html'
    form_class = ClickyJoinForm
    context_object_name = 'latest_room_list'

    def form_valid(self, form):
        room_name = form.cleaned_data['room_name']
        room_code = form.cleaned_data['room_code'].upper()

        return HttpResponseRedirect(reverse('clicky:detail', args=(room_code, slugify(room_name))))


class DetailView(generic.DetailView):
    model = Room
    template_name = 'clicky/detail.html'
    # slug_field = None
    message = None

    def get(self, request, *args, **kwargs):
        return super(DetailView, self).get(request, *args, **kwargs)

    def get_object(self):
        return validate_or_404(self.kwargs['room_id'], self.kwargs['slug'])

    def get_context_data(self, **kwargs):
        context = super(DetailView, self).get_context_data(**kwargs)
        context['message'] = self.message
        return context


def create(request):
    if request.method == 'POST':
        form = ClickyCreateForm(request.POST)
        if form.is_valid():
            room_text = form.cleaned_data['room_text']
            num_choices = form.cleaned_data['num_choices']
            can_see_results = form.cleaned_data['can_see_results']
            room = Room(room_text=room_text, pub_date=timezone.now(), can_see_results=can_see_results)
            # room = Room(room_text=room_text, pub_date=timezone.now())
            room.save()

            for x in range(num_choices):
                room.choice_set.create(choice_text=x + 1)

            request.session[room_admin_tag(room.id)] = True
            return HttpResponseRedirect(reverse('clicky:results', args=(room.id, room.slug())))
    else:
        form = ClickyCreateForm()
    own_rooms = [get_object_or_404(Room, id=str(name[10:])) for name in request.session.keys() if isinstance(name, str) and name.startswith('room_admin')]
    return render(request, 'clicky/create.html', {'form': form, 'own_rooms': own_rooms})


def vote(request, room_id, slug):
    room = validate_or_404(room_id, slug)

    if request.method == 'POST':
        message = ""
        vote_key = 'voted' + str(room_id)
        if vote_key in request.session and request.session[vote_key] == room.reset_id:
            message = "You already voted!"
        else:
            try:
                room.choice_set.filter(pk=request.POST['choice']).update(votes=F("votes") + 1)
                request.session[vote_key] = room.reset_id
                message = "Voted successfully!"
            except (KeyError, Choice.DoesNotExist):
                message = "You didn't select a choice."
        return render(request, 'clicky/detail.html', {
            'room': room,
            'message': message,
        })
    elif request.method == 'GET':
        return render(request, 'clicky/detail.html', {'room': room})


def room_admin_tag(room_id):
    return 'room_admin' + str(room_id)


def is_admin(room_id, session):
    return room_admin_tag(room_id) in session


def results(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if (is_admin(room_id, request.session)) or room.can_see_results:
        # TODO this is removed temporarily. In the future permanent room codes should be given to logged-in users
        # request.session.set_expiry(43200)  # access expires in 12 hours
        return render(request, 'clicky/results.html', {'room': room})
    return HttpResponseForbidden()


def results_data(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if (is_admin(room_id, request.session)) or room.can_see_results:
        # TODO trying to fix wrong vote count showing by seeing whether it is a problem with the JSON sent back
        # if not request.is_ajax():
        #     return HttpResponseRedirect(reverse('clicky:results', args=(room.id, slug)))
        room = Room.objects.get(pk=room_id)
        votes = []
        for choice in room.choice_set.order_by("id").iterator():
            choice.refresh_from_db()
            votes += str(choice.votes)
        data = {
            'votes': votes
        }
        return JsonResponse(data)
    return HttpResponseForbidden()


def reset(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if is_admin(room_id, request.session):
        room.reset_id += 1
        room.save()
        for choice in room.choice_set.iterator():
            choice.votes = 0
            choice.save()
        return HttpResponseRedirect(reverse('clicky:results', args=(room.id, slug)))
    return HttpResponseForbidden()
