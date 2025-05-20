from django import forms
from questions.models import Department, Major, QuestionCategory, Answer
from accounts.models import CustomUser
from .models import AdvisorProfile

class DepartmentForm(forms.ModelForm):
    class Meta:
        model = Department
        fields = ['name', 'description']
        widgets = {
            'description': forms.Textarea(attrs={'rows': 3}),
        }

class MajorForm(forms.ModelForm):
    class Meta:
        model = Major
        fields = ['name', 'department', 'description']
        widgets = {
            'description': forms.Textarea(attrs={'rows': 3}),
        }

class CategoryForm(forms.ModelForm):
    class Meta:
        model = QuestionCategory
        fields = ['name', 'description']
        widgets = {
            'description': forms.Textarea(attrs={'rows': 3}),
        }

class AdminAnswerForm(forms.ModelForm):
    class Meta:
        model = Answer
        fields = ['content']
        widgets = {
            'content': forms.Textarea(attrs={'rows': 5, 'placeholder': 'Nhập câu trả lời chính thức'}),
        }
        labels = {
            'content': 'Nội dung câu trả lời',
        }

class AdvisorProfileForm(forms.ModelForm):
    class Meta:
        model = AdvisorProfile
        fields = ['user', 'department', 'role']
        
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        # Chỉ hiển thị người dùng chưa có hồ sơ tư vấn viên
        existing_advisors = AdvisorProfile.objects.values_list('user', flat=True)
        self.fields['user'].queryset = CustomUser.objects.exclude(id__in=existing_advisors) 