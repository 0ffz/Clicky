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
    error_message = None

    def get(self, request, *args, **kwargs):
        return super(DetailView, self).get(request, *args, **kwargs)

    def get_object(self):
        return validate_or_404(self.kwargs['room_id'], self.kwargs['slug'])

    def get_context_data(self, **kwargs):
        context = super(DetailView, self).get_context_data(**kwargs)
        context['error_message'] = self.error_message
        return context


def get_room_admin(room_id):
    return 'room_admin' + str(room_id)


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

            request.session[get_room_admin(room.id)] = True
            return HttpResponseRedirect(reverse('clicky:results', args=(room.id, room.slug())))
    else:
        form = ClickyCreateForm()

    return render(request, 'clicky/create.html', {'form': form})


def vote(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if request.method == 'POST':
        error_message = ""
        try:
            selected_choice = room.choice_set.get(pk=request.POST['choice'])
        except (KeyError, Choice.DoesNotExist):
            error_message = "You didn't select a choice."
        else:
            vote_key = 'voted' + str(room_id)
            if vote_key in request.session and request.session[vote_key] == room.reset_id:
                error_message = "You already voted!"
            else:
                request.session[vote_key] = room.reset_id
                selected_choice.votes += 1
                selected_choice.save()
                error_message = "Voted successfully!"
        return render(request, 'clicky/detail.html', {
            'room': room,
            'error_message': error_message,
        })
    elif request.method == 'GET':
        return render(request, 'clicky/detail.html', {'room': room})


def results(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if (get_room_admin(room_id) in request.session) or room.can_see_results:
        # TODO this is removed temporarily. In the future permanent room codes should be given to logged-in users
        # request.session.set_expiry(43200)  # access expires in 12 hours
        return render(request, 'clicky/results.html', {'room': room})
    return HttpResponseForbidden()


def results_data(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if (get_room_admin(room_id) in request.session) or room.can_see_results:
        room = Room.objects.get(pk=room_id)
        votes = []
        for choice in room.choice_set.order_by("id").iterator():
            votes += str(choice.votes)
        data = {
            'votes': votes
        }
        return JsonResponse(data)
    return HttpResponseForbidden()


def reset(request, room_id, slug):
    room = validate_or_404(room_id, slug)
    if get_room_admin(room_id) in request.session:
        room.reset_id += 1
        room.save()
        for choice in room.choice_set.iterator():
            choice.votes = 0
            choice.save()
        return HttpResponseRedirect(reverse('clicky:results', args=(room.id, slug,)))
    return HttpResponseForbidden()
