<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt câu hỏi mới - Hệ thống tư vấn sinh viên</title>
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
        
        h2 {
            margin-bottom: 20px;
            color: #333;
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
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
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
        
        select.form-control {
            height: calc(2.25rem + 2px);
        }
        
        .text-danger {
            color: #dc3545;
        }
        
        .mt-1 {
            margin-top: 0.25rem;
        }
        
        .mb-3 {
            margin-bottom: 1rem;
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
        <h2>Đặt câu hỏi mới</h2>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/questions/ask" method="post" id="question-form" onsubmit="return validateForm()">
            <div class="form-group mb-3">
                <label for="title">Tiêu đề câu hỏi <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="title" name="title" 
                       placeholder="Nhập tiêu đề câu hỏi" maxlength="200" value="${param.title}">
                <div id="title-feedback" class="text-danger mt-1" style="display: none;">
                    Vui lòng nhập tiêu đề câu hỏi
                </div>
            </div>
            
            <div class="form-group mb-3">
                <label for="ask_content_main">Nội dung chi tiết <span class="text-danger">*</span></label>
                <textarea class="form-control" id="ask_content_main" name="content" rows="10" 
                          placeholder="Nội dung chi tiết câu hỏi (không để trống)">${param.content}</textarea>
                <div id="content-feedback" class="text-danger mt-1" style="display: none;">
                    Vui lòng nhập nội dung chi tiết câu hỏi
                </div>
            </div>
            
            <div class="form-group mb-3">
                <label for="category">Loại câu hỏi <span class="text-danger">*</span></label>
                <select class="form-control" id="category" name="categoryId">
                    <option value="">-- Chọn loại câu hỏi --</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                    </c:forEach>
                </select>
                <div id="category-feedback" class="text-danger mt-1" style="display: none;">
                    Vui lòng chọn loại câu hỏi
                </div>
            </div>
            
            <div class="form-group mb-3">
                <label for="major">Ngành học liên quan</label>
                <select class="form-control" id="major" name="majorId">
                    <option value="">-- Chọn ngành học (không bắt buộc) --</option>
                    <c:forEach items="${majors}" var="major">
                        <option value="${major.id}" ${param.majorId == major.id ? 'selected' : ''}>${major.name}</option>
                    </c:forEach>
                </select>
            </div>
            
            <button type="submit" class="btn" id="submit-button">
                Gửi câu hỏi
            </button>
            <a href="${pageContext.request.contextPath}/questions" class="btn btn-secondary">Hủy</a>
        </form>
    </div>

    <script>
    function validateForm() {
        var title = document.getElementById('title').value.trim();
        var content = document.getElementById('ask_content_main').value.trim();
        var category = document.getElementById('category').value;
        var isValid = true;
        
        // Validate title
        if (title === '') {
            document.getElementById('title-feedback').style.display = 'block';
            document.getElementById('title').focus();
            isValid = false;
        } else {
            document.getElementById('title-feedback').style.display = 'none';
        }
        
        // Validate content
        if (content === '') {
            document.getElementById('content-feedback').style.display = 'block';
            if (isValid) {
                document.getElementById('ask_content_main').focus();
                isValid = false;
            }
        } else {
            document.getElementById('content-feedback').style.display = 'none';
        }
        
        // Validate category
        if (category === '') {
            document.getElementById('category-feedback').style.display = 'block';
            if (isValid) {
                document.getElementById('category').focus();
                isValid = false;
            }
        } else {
            document.getElementById('category-feedback').style.display = 'none';
        }
        
        if (!isValid) {
            return false;
        }
        
        // Disable button to prevent double submission
        document.getElementById('submit-button').disabled = true;
        document.getElementById('submit-button').innerHTML = '<span class="spinner"></span> Đang gửi...';
        
        return true;
    }
    
    // Add input event listeners to hide feedback when user types
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('title').addEventListener('input', function() {
            if (this.value.trim() !== '') {
                document.getElementById('title-feedback').style.display = 'none';
            }
        });
        
        document.getElementById('ask_content_main').addEventListener('input', function() {
            if (this.value.trim() !== '') {
                document.getElementById('content-feedback').style.display = 'none';
            }
        });
        
        document.getElementById('category').addEventListener('change', function() {
            if (this.value !== '') {
                document.getElementById('category-feedback').style.display = 'none';
            }
        });
    });
    </script>
</body>
</html> 