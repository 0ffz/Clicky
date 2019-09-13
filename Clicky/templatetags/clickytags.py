from django import template

from Clicky.views import get_room_admin

register = template.Library()


@register.filter
def sort_by(queryset, order):
    return queryset.order_by(order)


@register.filter
def is_room_admin(session, room_id):
    return get_room_admin(room_id) in session
