from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.db.models import Q, Count
from .models import Question, Answer, Major, QuestionCategory, Department
from .forms import QuestionForm, AnswerForm, QuestionSearchForm
from admin_panel.models import AdvisorProfile

def home(request):
    # Lấy tất cả câu hỏi, không chỉ những câu đã trả lời
    recent_questions = Question.objects.all().order_by('-created_at')[:10]
    
    # Lấy các danh mục câu hỏi kèm số lượng câu hỏi
    categories = QuestionCategory.objects.annotate(question_count=Count('question')).order_by('-question_count')
    
    # Lấy các khoa/đơn vị và ngành học
    departments = Department.objects.all().prefetch_related('major_set')
    
    context = {
        'recent_questions': recent_questions,
        'categories': categories,
        'departments': departments,
    }
    
    return render(request, 'questions/home.html', context)

@login_required
def ask_question(request):
    if request.method == 'POST':
        form = QuestionForm(request.POST)
        if form.is_valid():
            question = form.save(commit=False)
            question.user = request.user
            question.save()
            messages.success(request, 'Câu hỏi của bạn đã được gửi thành công!')
            return redirect('my_questions')
    else:
        form = QuestionForm()
    return render(request, 'questions/ask_question.html', {'form': form})

@login_required
def my_questions(request):
    questions = Question.objects.filter(user=request.user).order_by('-created_at')
    return render(request, 'questions/my_questions.html', {'questions': questions})

def question_detail(request, pk):
    question = get_object_or_404(Question, pk=pk)
    answers = question.answers.all().order_by('created_at')
    
    # Tìm các câu hỏi liên quan (cùng danh mục hoặc cùng ngành)
    related_questions = Question.objects.filter(
        Q(category=question.category) | Q(major=question.major)
    ).exclude(id=question.id).distinct().order_by('-created_at')[:4]
    
    # Kiểm tra xem người dùng có phải là tư vấn viên không
    can_answer = False
    if request.user.is_authenticated:
        try:
            advisor = request.user.advisorprofile
            can_answer = True
        except:
            can_answer = False
    
    if request.method == 'POST' and request.user.is_authenticated:
        if not can_answer:
            messages.error(request, 'Bạn không có quyền trả lời câu hỏi. Chỉ tư vấn viên mới có thể trả lời.')
            return redirect('question_detail', pk=pk)
            
        form = AnswerForm(request.POST)
        if form.is_valid():
            answer = form.save(commit=False)
            answer.question = question
            answer.user = request.user
            answer.save()
            
            # Cập nhật trạng thái câu hỏi thành đã trả lời
            question.status = 'answered'
            question.save()
            
            messages.success(request, 'Câu trả lời của bạn đã được gửi thành công!')
            return redirect('question_detail', pk=pk)
    else:
        form = AnswerForm()
    
    return render(request, 'questions/question_detail.html', {
        'question': question,
        'answers': answers,
        'related_questions': related_questions,
        'form': form,
        'can_answer': can_answer
    })

def search_questions(request):
    form = QuestionSearchForm(request.GET)
    questions = Question.objects.all()
    
    if form.is_valid():
        query = form.cleaned_data.get('query')
        category = form.cleaned_data.get('category')
        major = form.cleaned_data.get('major')
        status = form.cleaned_data.get('status')
        
        if query:
            questions = questions.filter(
                Q(title__icontains=query) | Q(content__icontains=query)
            )
        
        if category:
            questions = questions.filter(category=category)
            
        if major:
            questions = questions.filter(major=major)
            
        if status:
            questions = questions.filter(status=status)
    
    questions = questions.order_by('-created_at')
    
    return render(request, 'questions/search_questions.html', {
        'form': form,
        'questions': questions
    })

def category_questions(request, category_id):
    category = get_object_or_404(QuestionCategory, id=category_id)
    questions = Question.objects.filter(category=category).order_by('-created_at')
    
    return render(request, 'questions/category_questions.html', {
        'category': category,
        'questions': questions
    })

def major_questions(request, major_id):
    major = get_object_or_404(Major, id=major_id)
    questions = Question.objects.filter(major=major).order_by('-created_at')
    
    return render(request, 'questions/major_questions.html', {
        'major': major,
        'questions': questions
    })

def advisor_list(request):
    """Hiển thị danh sách tư vấn viên"""
    advisors = AdvisorProfile.objects.all().select_related('user', 'department')
    
    # Lọc theo khoa/đơn vị nếu có
    department_id = request.GET.get('department')
    if department_id:
        advisors = advisors.filter(department_id=department_id)
    
    # Lấy danh sách các khoa/đơn vị để hiển thị bộ lọc
    departments = Department.objects.all()
    
    context = {
        'advisors': advisors,
        'departments': departments,
        'selected_department': department_id
    }
    
    return render(request, 'questions/advisor_list.html', context)
