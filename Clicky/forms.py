from captcha.fields import ReCaptchaField
from captcha.widgets import ReCaptchaV2Invisible
from django import forms
from django.http import Http404


class ClickyCreateForm(forms.Form):
    room_text = forms.CharField(label='Room Name', max_length=20)
    num_choices = forms.IntegerField(label='Choices', min_value=2, max_value=10)
    captcha = ReCaptchaField(widget=ReCaptchaV2Invisible)


class ClickyJoinForm(forms.Form):
    room_name = forms.CharField(label='Room Name', max_length=20)
    room_code = forms.IntegerField(label='Code', min_value=0)

    def clean(self):
        cleaned_data = super().clean()
        room_name = cleaned_data.get("room_name")
        room_code = cleaned_data.get("room_code")
        try:
            from Clicky.views import validate_or_404
            validate_or_404(room_code, room_name)
        except Http404:
            # raise forms.ValidationError('The room name or code is incorrect')
            self.add_error('room_code', 'The room name or code is incorrect')
