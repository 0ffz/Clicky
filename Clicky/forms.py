from django import forms


class ClickyCreateForm(forms.Form):
    room_text = forms.CharField(label='Room Text', max_length=20)
    num_choices = forms.IntegerField(label='Choices', min_value=2, max_value=10)
