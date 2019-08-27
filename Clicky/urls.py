from django.urls import path

from . import views

app_name = 'clicky'
urlpatterns = [
    path('', views.IndexView.as_view(), name='index'),
    path('<int:pk>/', views.DetailView.as_view(), name='detail'),
    path('<int:room_id>/results/', views.results, name='results'),
    path('<int:room_id>/vote/', views.vote, name='vote'),
    path('<int:room_id>/reset/', views.reset, name='reset'),
    path('create/', views.create, name='create'),
]