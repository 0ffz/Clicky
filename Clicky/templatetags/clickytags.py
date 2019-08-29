from django import template

from Clicky.models import Room

register = template.Library()


@register.filter
def sort_by(queryset, order):
    return queryset.order_by(order)
