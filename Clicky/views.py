from django.http import HttpResponseRedirect
from django.shortcuts import get_object_or_404, render
from django.urls import reverse
from django.views import generic

from .models import Choice, Room


class IndexView(generic.ListView):
    template_name = 'clicky/index.html'
    context_object_name = 'latest_room_list'

    def get_queryset(self):
        """Return the last five published rooms."""
        return Room.objects.order_by('-pub_date')[:5]


class DetailView(generic.DetailView):
    model = Room
    template_name = 'clicky/detail.html'


class ResultsView(generic.DetailView):
    model = Room
    template_name = 'clicky/results.html'


def vote(request, room_id):
    room = get_object_or_404(Room, pk=room_id)
    try:
        selected_choice = room.choice_set.get(pk=request.POST['choice'])
    except (KeyError, Choice.DoesNotExist):
        # Redisplay the room voting form.
        return render(request, 'clicky/detail.html', {
            'room': room,
            'error_message': "You didn't select a choice.",
        })
    else:
        selected_choice.votes += 1
        selected_choice.save()
        # Always return an HttpResponseRedirect after successfully dealing
        # with POST data. This prevents data from being posted twice if a
        # user hits the Back button.
        return HttpResponseRedirect(reverse('Clicky:results', args=(room.id,)))
