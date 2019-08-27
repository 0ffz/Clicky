from django import template
from django.shortcuts import get_object_or_404

from Clicky.models import Room

register = template.Library()


@register.filter(name='get_choices')
def get_choices(room_id):
    room = Room.objects.get(pk=room_id)
    values = ""
    for choice in room.choice_set.iterator():
        values += str(choice.votes) + ","
    return values
