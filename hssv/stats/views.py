from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from django.db.models import Count, Q
from questions.models import Question, Answer, Department, Major, QuestionCategory
from admin_panel.models import AdvisorProfile
from datetime import datetime

def is_admin(user):
    """Kiểm tra xem người dùng có phải là admin hay không"""
    try:
        advisor = AdvisorProfile.objects.get(user=user)
        return advisor.role == 'admin'
    except AdvisorProfile.DoesNotExist:
        return False

@login_required
def statistics_dashboard(request):
    if not is_admin(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang thống kê.')
        return redirect('home')
    
    # Thống kê hiệu quả làm việc của các khoa
    departments = Department.objects.annotate(
        total_questions=Count('major__question', distinct=True),
        answered_questions=Count('major__question', filter=Q(major__question__status='answered'), distinct=True)
    )
    
    # Tính tỷ lệ trả lời
    for dept in departments:
        if dept.total_questions > 0:
            dept.response_rate = (dept.answered_questions / dept.total_questions) * 100
        else:
            dept.response_rate = 0
    
    # Thống kê tổng số câu hỏi
    total_questions = Question.objects.count()
    
    # Thống kê số lượng câu hỏi từ đầu năm
    current_year = datetime.now().year
    questions_this_year = Question.objects.filter(
        created_at__year=current_year
    ).count()
    
    # Thống kê đề tài được quan tâm nhiều nhất
    popular_categories = QuestionCategory.objects.annotate(
        question_count=Count('question')
    ).order_by('-question_count')[:5]
    
    return render(request, 'stats/dashboard.html', {
        'departments': departments,
        'total_questions': total_questions,
        'questions_this_year': questions_this_year,
        'popular_categories': popular_categories
    })

@login_required
def department_statistics(request, pk):
    if not is_admin(request.user):
        messages.error(request, 'Bạn không có quyền truy cập trang thống kê.')
        return redirect('home')
    
    department = Department.objects.get(pk=pk)
    
    # Thống kê câu hỏi theo ngành học
    majors = Major.objects.filter(department=department).annotate(
        total_questions=Count('question', distinct=True),
        answered_questions=Count('question', filter=Q(question__status='answered'), distinct=True)
    )
    
    # Tính tỷ lệ trả lời
    for major in majors:
        if major.total_questions > 0:
            major.response_rate = (major.answered_questions / major.total_questions) * 100
        else:
            major.response_rate = 0
    
    # Thống kê câu hỏi theo tháng
    questions_by_month = []
    current_year = datetime.now().year
    
    for month in range(1, 13):
        count = Question.objects.filter(
            major__department=department,
            created_at__year=current_year,
            created_at__month=month
        ).count()
        
        questions_by_month.append({
            'month': month,
            'count': count
        })
    
    return render(request, 'stats/department_statistics.html', {
        'department': department,
        'majors': majors,
        'questions_by_month': questions_by_month
    })
