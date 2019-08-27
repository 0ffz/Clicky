from django.db import models
from django.utils.text import slugify


class Room(models.Model):
    room_text = models.CharField(max_length=200)
    pub_date = models.DateTimeField('date published')
    reset_id = models.IntegerField(default=0)

    def slug(self):
        return slugify(self.room_text)

    def __str__(self):
        return self.room_text


class Choice(models.Model):
    room = models.ForeignKey(Room, on_delete=models.CASCADE)
    choice_text = models.CharField(max_length=200)
    votes = models.IntegerField(default=0)

    def __str__(self):
        return self.choice_text
