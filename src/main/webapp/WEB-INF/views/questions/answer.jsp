<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trả lời câu hỏi - Hệ thống tư vấn sinh viên</title>
    <style>
        /* Reset CSS */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f5f5f5;
            padding: 20px;
        }
        
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        
        .btn {
            display: inline-block;
            padding: 8px 16px;
            background-color: #4a89dc;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }
        
        .btn:hover {
            background-color: #3b6fc1;
        }
        
        .btn-secondary {
            background-color: #6c757d;
        }
        
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        
        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
        }
        
        .card {
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 20px;
            background-color: #fff;
        }
        
        .card-header {
            padding: 12px 15px;
            border-bottom: 1px solid #ddd;
            background-color: #f8f9fa;
        }
        
        .bg-primary {
            background-color: #4a89dc !important;
            color: white !important;
        }
        
        .bg-light {
            background-color: #f8f9fa !important;
        }
        
        .card-body {
            padding: 15px;
        }
        
        .mb-0 {
            margin-bottom: 0 !important;
        }
        
        .mb-2 {
            margin-bottom: 0.5rem !important;
        }
        
        .mb-3 {
            margin-bottom: 1rem !important;
        }
        
        .mb-4 {
            margin-bottom: 1.5rem !important;
        }
        
        .mt-1 {
            margin-top: 0.25rem !important;
        }
        
        .mt-2 {
            margin-top: 0.5rem !important;
        }
        
        .me-1 {
            margin-right: 0.25rem !important;
        }
        
        .me-2 {
            margin-right: 0.5rem !important;
        }
        
        .ms-1 {
            margin-left: 0.25rem !important;
        }
        
        .ms-2 {
            margin-left: 0.5rem !important;
        }
        
        .p-0 {
            padding: 0 !important;
        }
        
        .p-4 {
            padding: 1.5rem !important;
        }
        
        .d-flex {
            display: flex !important;
        }
        
        .justify-content-between {
            justify-content: space-between !important;
        }
        
        .align-items-center {
            align-items: center !important;
        }
        
        .text-white {
            color: white !important;
        }
        
        .text-muted {
            color: #6c757d !important;
        }
        
        .text-danger {
            color: #dc3545 !important;
        }
        
        .text-center {
            text-align: center !important;
        }
        
        .badge {
            display: inline-block;
            padding: 0.25em 0.6em;
            font-size: 75%;
            font-weight: 700;
            line-height: 1;
            text-align: center;
            white-space: nowrap;
            vertical-align: baseline;
            border-radius: 0.25rem;
        }
        
        .bg-warning {
            background-color: #ffc107 !important;
        }
        
        .bg-success {
            background-color: #28a745 !important;
        }
        
        .bg-info {
            background-color: #17a2b8 !important;
            color: white !important;
        }
        
        .bg-secondary {
            background-color: #6c757d !important;
            color: white !important;
        }
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        .form-control {
            display: block;
            width: 100%;
            padding: 0.375rem 0.75rem;
            font-size: 1rem;
            line-height: 1.5;
            color: #495057;
            background-color: #fff;
            background-clip: padding-box;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        }
        
        .form-control:focus {
            color: #495057;
            background-color: #fff;
            border-color: #80bdff;
            outline: 0;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        
        textarea.form-control {
            height: auto;
            min-height: 200px;
        }
        
        .alert {
            position: relative;
            padding: 0.75rem 1.25rem;
            margin-bottom: 1rem;
            border: 1px solid transparent;
            border-radius: 0.25rem;
        }
        
        .alert-danger {
            color: #721c24;
            background-color: #f8d7da;
            border-color: #f5c6cb;
        }
        
        .alert-info {
            color: #0c5460;
            background-color: #d1ecf1;
            border-color: #bee5eb;
        }
        
        .list-group {
            display: flex;
            flex-direction: column;
            padding-left: 0;
            margin-bottom: 0;
        }
        
        .list-group-flush {
            border-radius: 0;
        }
        
        .list-group-item {
            position: relative;
            display: block;
            padding: 0.75rem 1.25rem;
            background-color: #fff;
            border: 1px solid rgba(0, 0, 0, 0.125);
            border-width: 0 0 1px;
        }
        
        .list-group-item:last-child {
            border-bottom-width: 0;
        }
        
        small {
            font-size: 80%;
        }
        
        .spinner {
            display: inline-block;
            width: 1rem;
            height: 1rem;
            border: 0.2em solid currentColor;
            border-right-color: transparent;
            border-radius: 50%;
            animation: spinner-border .75s linear infinite;
            vertical-align: text-bottom;
        }
        
        @keyframes spinner-border {
            to { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/questions" class="btn btn-secondary btn-sm">
                ← Quay lại danh sách
            </a>
        </div>

        <div class="card mb-4">
            <div class="card-header bg-primary d-flex justify-content-between align-items-center">
                <h4 class="mb-0">${question.title}</h4>
                <c:choose>
                    <c:when test="${question.status == 'PENDING'}">
                        <span class="badge bg-warning">Chưa trả lời</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-success">Đã trả lời</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    ${question.content}
                </div>
                <div class="d-flex justify-content-between">
                    <div>
                        <c:if test="${not empty question.category}">
                            <span class="badge bg-info me-2">${question.category.name}</span>
                        </c:if>
                        <c:if test="${not empty question.major}">
                            <span class="badge bg-secondary">${question.major.name}</span>
                        </c:if>
                    </div>
                    <div class="text-muted">
                        <small>
                            Người đăng: ${question.user.fullName} <!-- Assuming CustomUser has fullName -->
                        </small>
                        <small class="ms-2">
                            Ngày đăng: <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Câu trả lời hiện tại</h5>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${not empty answers}">
                        <div class="list-group list-group-flush">
                            <c:forEach items="${answers}" var="answer">
                                <div class="list-group-item ${answer.user.advisorProfile != null ? 'bg-light' : ''}">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <div>
                                            <strong>${answer.user.fullName}</strong> <!-- Assuming CustomUser has fullName -->
                                            <c:if test="${answer.user.advisorProfile != null}">
                                                <span class="badge bg-primary ms-1">${answer.user.advisorProfile.role}</span>
                                                <c:if test="${not empty answer.user.advisorProfile.department}">
                                                    <span class="badge bg-info ms-1">${answer.user.advisorProfile.department.name}</span>
                                                </c:if>
                                            </c:if>
                                        </div>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${answer.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </small>
                                    </div>
                                    <div class="mb-0">
                                        ${answer.content}
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="p-4 text-center">
                            <div class="alert alert-info mb-0">
                                Chưa có câu trả lời nào.
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <c:if test="${sessionScope.isAdvisor == true || sessionScope.isAdmin == true}"> 
            <div class="card">
                <div class="card-header bg-light">
                    <h5 class="mb-0">Trả lời câu hỏi</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/questions/answer" 
                          method="post" id="answer-form" onsubmit="return validateAnswerForm()">
                        <input type="hidden" name="questionId" value="${question.id}"/>
                        <div class="form-group mb-3">
                            <label for="answer_content_main">Nội dung trả lời <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="answer_content_main" name="content" rows="10" 
                                      placeholder="Nội dung trả lời (tối thiểu 10 ký tự, không để trống)">${param.content}</textarea>
                            <div id="answer-feedback" class="text-danger mt-1" style="display: none;">
                                Vui lòng nhập nội dung trả lời (tối thiểu 10 ký tự)
                            </div>
                        </div>
                        
                        <button type="submit" class="btn" id="answer-submit">
                            Gửi câu trả lời
                        </button>
                        <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" 
                           class="btn btn-secondary">Làm mới</a>
                    </form>
                </div>
            </div>
        </c:if>

    </div>

    <script>
    function validateAnswerForm() {
        var content = document.getElementById('answer_content_main').value.trim();
        var feedback = document.getElementById('answer-feedback');
        var submitBtn = document.getElementById('answer-submit');
        
        if (content === '' || content.length < 10) {
            feedback.style.display = 'block';
            document.getElementById('answer_content_main').focus();
            return false;
        } else {
            feedback.style.display = 'none';
            
            // Disable button to prevent double submission
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner"></span> Đang gửi...';
            
            return true;
        }
    }
    
    document.addEventListener('DOMContentLoaded', function() {
        var answerContent = document.getElementById('answer_content_main');
        if (answerContent) {
             answerContent.addEventListener('input', function() {
                var content = this.value.trim();
                if (content && content.length >= 10) {
                    document.getElementById('answer-feedback').style.display = 'none';
                } else {
                    document.getElementById('answer-feedback').style.display = 'block';
                }
            });
        }
    });
    </script>
</body>
</html> 
