# Generated by Django 2.2.4 on 2019-08-27 22:00

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('Clicky', '0003_auto_20190827_1733'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='room',
            name='room_id',
        ),
    ]
