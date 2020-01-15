from django import template

from Clicky.views import is_admin

register = template.Library()


@register.filter
def sort_by(queryset, order):
    return queryset.order_by(order)


@register.filter
def is_room_admin(session, room_id):
    return is_admin(room_id, session)
