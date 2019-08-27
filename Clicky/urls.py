from django.urls import path

from . import views

app_name = 'clicky'
urlpatterns = [
    path('', views.IndexView.as_view(), name='index'),
    path('<int:pk>/<slug:slug>/', views.DetailView.as_view(), name='detail'),
    path('<int:room_id>/<slug:slug>/results/', views.results, name='results'),
    path('<int:room_id>/<slug:slug>/results/data', views.results_data, name='results_data'),
    path('<int:room_id>/<slug:slug>/vote/', views.vote, name='vote'),
    path('<int:room_id>/<slug:slug>/reset/', views.reset, name='reset'),
    path('create/', views.create, name='create'),
]
