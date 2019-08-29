from django import template

from Clicky.models import Room

register = template.Library()


@register.filter(name='get_choices')
def get_choices(room_id):
    room = Room.objects.get(pk=room_id)
    values = ""
    for choice in room.choice_set.order_by("id").iterator():
        values += str(choice.votes) + ","
    return values


@register.filter
def sort_by(queryset, order):
    return queryset.order_by(order)
