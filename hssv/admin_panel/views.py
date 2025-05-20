from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.db.models import Count, Q
from questions.models import Question, Answer, Department, Major, QuestionCategory
from questions.forms import QuestionForm, AnswerForm
from accounts.models import CustomUser
from .models import AdvisorProfile
from .forms import DepartmentForm, MajorForm, CategoryForm, AdminAnswerForm, AdvisorProfileForm

def is_advisor(user):
    """Kiểm tra xem người dùng có phải là tư vấn viên hay không"""
    try:
        advisor = AdvisorProfile.objects.get(user=user)
        return True
    except AdvisorProfile.DoesNotExist:
        return False

@login_required
def admin_dashboard(request):
    # Kiểm tra quyền quản trị
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    # Thống kê
    total_questions = Question.objects.count()
    pending_questions = Question.objects.filter(status='pending').count()
    answered_questions = Question.objects.filter(status='answered').count()
    
    # Nếu là ban tư vấn khoa, chỉ hiển thị câu hỏi liên quan đến khoa
    if advisor.role == 'advisor' and advisor.department:
        pending_questions = Question.objects.filter(
            status='pending',
            major__department=advisor.department
        ).count()
        
        answered_questions = Question.objects.filter(
            status='answered',
            major__department=advisor.department
        ).count()
        
        total_questions = pending_questions + answered_questions
    
    return render(request, 'admin_panel/dashboard.html', {
        'total_questions': total_questions,
        'pending_questions': pending_questions,
        'answered_questions': answered_questions,
        'advisor': advisor
    })

@login_required
def manage_questions(request):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    # Lọc câu hỏi theo quyền
    if advisor.role == 'admin':
        questions = Question.objects.all().order_by('-created_at')
    else:
        questions = Question.objects.filter(
            major__department=advisor.department
        ).order_by('-created_at')
    
    # Lọc theo trạng thái nếu có
    status = request.GET.get('status')
    if status:
        questions = questions.filter(status=status)
    
    return render(request, 'admin_panel/manage_questions.html', {
        'questions': questions,
        'status': status
    })

@login_required
def answer_question(request, pk):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    question = get_object_or_404(Question, pk=pk)
    
    # Kiểm tra quyền trả lời
    if advisor.role != 'admin' and (not question.major or question.major.department != advisor.department):
        messages.error(request, 'Bạn không có quyền trả lời câu hỏi này.')
        return redirect('manage_questions')
    
    if request.method == 'POST':
        form = AdminAnswerForm(request.POST)
        if form.is_valid():
            answer = form.save(commit=False)
            answer.question = question
            answer.user = request.user
            answer.save()
            
            # Cập nhật trạng thái câu hỏi
            question.status = 'answered'
            question.save()
            
            messages.success(request, 'Câu trả lời của bạn đã được gửi thành công!')
            return redirect('manage_questions')
    else:
        form = AdminAnswerForm()
    
    # Lấy các câu trả lời hiện có
    answers = question.answers.all().order_by('created_at')
    
    return render(request, 'admin_panel/answer_question.html', {
        'question': question,
        'answers': answers,
        'form': form
    })

@login_required
def manage_departments(request):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    departments = Department.objects.all()
    
    if request.method == 'POST':
        form = DepartmentForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, 'Thêm khoa/đơn vị thành công!')
            return redirect('manage_departments')
    else:
        form = DepartmentForm()
    
    return render(request, 'admin_panel/manage_departments.html', {
        'departments': departments,
        'form': form
    })

@login_required
def edit_department(request, pk):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    department = get_object_or_404(Department, pk=pk)
    
    if request.method == 'POST':
        form = DepartmentForm(request.POST, instance=department)
        if form.is_valid():
            form.save()
            messages.success(request, 'Cập nhật khoa/đơn vị thành công!')
            return redirect('manage_departments')
    else:
        form = DepartmentForm(instance=department)
    
    return render(request, 'admin_panel/edit_department.html', {
        'form': form,
        'department': department
    })

@login_required
def manage_majors(request):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    majors = Major.objects.all()
    
    if request.method == 'POST':
        form = MajorForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, 'Thêm ngành học thành công!')
            return redirect('manage_majors')
    else:
        form = MajorForm()
    
    return render(request, 'admin_panel/manage_majors.html', {
        'majors': majors,
        'form': form
    })

@login_required
def edit_major(request, pk):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    major = get_object_or_404(Major, pk=pk)
    
    if request.method == 'POST':
        form = MajorForm(request.POST, instance=major)
        if form.is_valid():
            form.save()
            messages.success(request, 'Cập nhật ngành học thành công!')
            return redirect('manage_majors')
    else:
        form = MajorForm(instance=major)
    
    return render(request, 'admin_panel/edit_major.html', {
        'form': form,
        'major': major
    })

@login_required
def manage_categories(request):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    categories = QuestionCategory.objects.all()
    
    if request.method == 'POST':
        form = CategoryForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, 'Thêm loại câu hỏi thành công!')
            return redirect('manage_categories')
    else:
        form = CategoryForm()
    
    return render(request, 'admin_panel/manage_categories.html', {
        'categories': categories,
        'form': form
    })

@login_required
def edit_category(request, pk):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý danh mục.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    category = get_object_or_404(QuestionCategory, pk=pk)
    
    if request.method == 'POST':
        form = CategoryForm(request.POST, instance=category)
        if form.is_valid():
            form.save()
            messages.success(request, 'Cập nhật loại câu hỏi thành công!')
            return redirect('manage_categories')
    else:
        form = CategoryForm(instance=category)
    
    return render(request, 'admin_panel/edit_category.html', {
        'form': form,
        'category': category
    })

@login_required
def manage_advisors(request):
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền quản lý tư vấn viên.')
            return redirect('admin_dashboard')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    advisors = AdvisorProfile.objects.all()
    
    if request.method == 'POST':
        form = AdvisorProfileForm(request.POST)
        if form.is_valid():
            form.save()
            messages.success(request, 'Thêm tư vấn viên thành công!')
            return redirect('manage_advisors')
    else:
        form = AdvisorProfileForm()
    
    return render(request, 'admin_panel/manage_advisors.html', {
        'advisors': advisors,
        'form': form
    })

@login_required
def delete_department(request, pk):
    """Xóa khoa/đơn vị"""
    department = get_object_or_404(Department, pk=pk)
    
    # Kiểm tra xem người dùng có phải là admin không
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền xóa khoa/đơn vị.')
            return redirect('manage_departments')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền xóa khoa/đơn vị.')
        return redirect('manage_departments')
    
    # Kiểm tra xem khoa/đơn vị có ngành học hoặc tư vấn viên không
    major_count = Major.objects.filter(department=department).count()
    advisor_count = AdvisorProfile.objects.filter(department=department).count()
    
    if major_count > 0 or advisor_count > 0:
        messages.error(request, f'Không thể xóa khoa/đơn vị "{department.name}" vì có {major_count} ngành học và {advisor_count} tư vấn viên liên quan.')
        return redirect('manage_departments')
    
    # Xóa khoa/đơn vị
    department_name = department.name
    department.delete()
    messages.success(request, f'Đã xóa khoa/đơn vị "{department_name}" thành công.')
    
    return redirect('manage_departments')

@login_required
def delete_major(request, pk):
    """Xóa ngành học"""
    major = get_object_or_404(Major, pk=pk)
    
    # Kiểm tra xem người dùng có phải là admin không
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền xóa ngành học.')
            return redirect('manage_majors')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền xóa ngành học.')
        return redirect('manage_majors')
    
    # Kiểm tra xem ngành học có câu hỏi không
    question_count = Question.objects.filter(major=major).count()
    
    if question_count > 0:
        messages.error(request, f'Không thể xóa ngành học "{major.name}" vì có {question_count} câu hỏi liên quan.')
        return redirect('manage_majors')
    
    # Xóa ngành học
    major_name = major.name
    major.delete()
    messages.success(request, f'Đã xóa ngành học "{major_name}" thành công.')
    
    return redirect('manage_majors')

@login_required
def delete_category(request, pk):
    """Xóa loại câu hỏi"""
    category = get_object_or_404(QuestionCategory, pk=pk)
    
    # Kiểm tra xem người dùng có phải là admin không
    try:
        advisor = AdvisorProfile.objects.get(user=request.user)
        if advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền xóa loại câu hỏi.')
            return redirect('manage_categories')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền xóa loại câu hỏi.')
        return redirect('manage_categories')
    
    # Kiểm tra xem loại câu hỏi có câu hỏi không
    question_count = Question.objects.filter(category=category).count()
    
    if question_count > 0:
        messages.error(request, f'Không thể xóa loại câu hỏi "{category.name}" vì có {question_count} câu hỏi liên quan.')
        return redirect('manage_categories')
    
    # Xóa loại câu hỏi
    category_name = category.name
    category.delete()
    messages.success(request, f'Đã xóa loại câu hỏi "{category_name}" thành công.')
    
    return redirect('manage_categories')

@login_required
def delete_advisor(request, pk):
    """Xóa tư vấn viên"""
    advisor = get_object_or_404(AdvisorProfile, pk=pk)
    
    # Kiểm tra xem người dùng có phải là admin không
    try:
        user_advisor = AdvisorProfile.objects.get(user=request.user)
        if user_advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền xóa tư vấn viên.')
            return redirect('manage_advisors')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền xóa tư vấn viên.')
        return redirect('manage_advisors')
    
    # Không cho phép xóa chính mình
    if advisor.user == request.user:
        messages.error(request, 'Bạn không thể xóa chính mình.')
        return redirect('manage_advisors')
    
    # Xóa tư vấn viên
    advisor_name = advisor.user.get_full_name() or advisor.user.username
    advisor.delete()
    messages.success(request, f'Đã xóa tư vấn viên "{advisor_name}" thành công.')
    
    return redirect('manage_advisors')

@login_required
def edit_advisor(request, pk):
    """Chỉnh sửa thông tin tư vấn viên"""
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    try:
        user_advisor = AdvisorProfile.objects.get(user=request.user)
        if user_advisor.role != 'admin':
            messages.error(request, 'Bạn không có quyền chỉnh sửa tư vấn viên.')
            return redirect('manage_advisors')
    except AdvisorProfile.DoesNotExist:
        messages.error(request, 'Bạn không có quyền truy cập trang quản trị.')
        return redirect('home')
    
    advisor = get_object_or_404(AdvisorProfile, pk=pk)
    
    if request.method == 'POST':
        form = AdvisorProfileForm(request.POST, instance=advisor)
        if form.is_valid():
            form.save()
            messages.success(request, f'Đã cập nhật thông tin tư vấn viên thành công.')
            return redirect('manage_advisors')
    else:
        form = AdvisorProfileForm(instance=advisor)
    
    return render(request, 'admin_panel/edit_advisor.html', {
        'form': form,
        'advisor': advisor
    })

@login_required
def edit_question(request, pk):
    """Chỉnh sửa câu hỏi"""
    question = get_object_or_404(Question, pk=pk)
    
    # Kiểm tra xem người dùng có phải là tư vấn viên không
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền chỉnh sửa câu hỏi.')
        return redirect('question_detail', pk=pk)
    
    if request.method == 'POST':
        form = QuestionForm(request.POST, instance=question)
        if form.is_valid():
            form.save()
            messages.success(request, f'Đã cập nhật câu hỏi "{question.title}" thành công.')
            return redirect('manage_questions')
    else:
        form = QuestionForm(instance=question)
    
    return render(request, 'admin_panel/edit_question.html', {
        'form': form,
        'question': question
    })

@login_required
def delete_question(request, pk):
    """Xóa câu hỏi"""
    question = get_object_or_404(Question, pk=pk)
    
    # Kiểm tra xem người dùng có phải là tư vấn viên không
    if not is_advisor(request.user):
        messages.error(request, 'Bạn không có quyền xóa câu hỏi.')
        return redirect('question_detail', pk=pk)
    
    # Xóa câu hỏi
    question_title = question.title
    question.delete()
    messages.success(request, f'Đã xóa câu hỏi "{question_title}" thành công.')
    
    return redirect('manage_questions')
