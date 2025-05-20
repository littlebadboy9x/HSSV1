from django import forms
from .models import Question, Answer, Major, QuestionCategory

class QuestionForm(forms.ModelForm):
    class Meta:
        model = Question
        fields = ['title', 'content', 'major', 'category']
        widgets = {
            'content': forms.Textarea(attrs={'rows': 5}),
        }
        
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['major'].queryset = Major.objects.all().order_by('name')
        self.fields['category'].queryset = QuestionCategory.objects.all().order_by('name')
        
        # Thêm các placeholder
        self.fields['title'].widget.attrs.update({'placeholder': 'Nhập tiêu đề câu hỏi'})
        self.fields['content'].widget.attrs.update({'placeholder': 'Nhập nội dung câu hỏi chi tiết'})

class AnswerForm(forms.ModelForm):
    class Meta:
        model = Answer
        fields = ['content']
        widgets = {
            'content': forms.Textarea(attrs={'rows': 4, 'placeholder': 'Nhập câu trả lời của bạn'}),
        }

class QuestionSearchForm(forms.Form):
    query = forms.CharField(
        label='Tìm kiếm',
        max_length=100,
        required=False,
        widget=forms.TextInput(attrs={'placeholder': 'Nhập từ khóa tìm kiếm'})
    )
    category = forms.ModelChoiceField(
        label='Loại câu hỏi',
        queryset=QuestionCategory.objects.all(),
        required=False,
        empty_label="Tất cả loại câu hỏi"
    )
    major = forms.ModelChoiceField(
        label='Ngành học',
        queryset=Major.objects.all(),
        required=False,
        empty_label="Tất cả ngành học"
    )
    status = forms.ChoiceField(
        label='Trạng thái',
        choices=(
            ('', 'Tất cả'),
            ('pending', 'Chưa trả lời'),
            ('answered', 'Đã trả lời'),
        ),
        required=False
    ) 