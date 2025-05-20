<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${question.title} - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- Thêm CKEditor -->
    <script src="https://cdn.ckeditor.com/ckeditor5/27.1.0/classic/ckeditor.js"></script>
    <style>
        .question-content img, .answer-content img {
            max-width: 100%;
            height: auto;
        }
        .badge-pending {
            background-color: #ffc107;
            color: #212529;
        }
        .badge-answered {
            background-color: #28a745;
            color: #fff;
        }
    </style>
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/questions">Câu hỏi</a></li>
                <li class="breadcrumb-item active" aria-current="page">${question.title}</li>
            </ol>
        </nav>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>
        
        <!-- Chi tiết câu hỏi -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h2 class="mb-0">${question.title}</h2>
                <span class="badge ${question.status == 'answered' ? 'badge-answered' : 'badge-pending'}">
                    ${question.status == 'answered' ? 'Đã trả lời' : 'Đang chờ'}
                </span>
            </div>
            <div class="card-body">
                <div class="question-content">
                    ${question.content}
                </div>
                <hr>
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <small class="text-muted">
                            Đăng bởi: ${question.user.fullName} |
                            <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                        </small>
                    </div>
                    <div>
                        <c:if test="${not empty question.category}">
                            <span class="badge badge-info">${question.category.name}</span>
                        </c:if>
                        <c:if test="${not empty question.major}">
                            <span class="badge badge-secondary">${question.major.name}</span>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Danh sách câu trả lời -->
        <h3 class="mb-3">Câu trả lời (${answers.size()})</h3>
        
        <c:choose>
            <c:when test="${empty answers}">
                <div class="alert alert-info">
                    Câu hỏi này chưa có câu trả lời nào.
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${answers}" var="answer" varStatus="status">
                    <div class="card mb-3">
                        <div class="card-header bg-light d-flex justify-content-between align-items-center">
                            <div>
                                <strong>${answer.user.fullName}</strong>
                                <c:if test="${isAdvisor && answer.user.id == user.id}">
                                    <span class="badge badge-primary">Bạn</span>
                                </c:if>
                            </div>
                            <small class="text-muted">
                                <fmt:formatDate value="${answer.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </small>
                        </div>
                        <div class="card-body">
                            <div class="answer-content">
                                ${answer.content}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        
        <!-- Form trả lời câu hỏi (chỉ hiển thị cho cố vấn) -->
        <c:if test="${isAdvisor}">
            <div class="card mt-4">
                <div class="card-header">
                    <h4 class="mb-0">Trả lời câu hỏi</h4>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/questions/detail">
                        <input type="hidden" name="questionId" value="${question.id}">
                        
                        <div class="form-group">
                            <label for="content">Nội dung câu trả lời</label>
                            <textarea class="form-control" id="content" name="content" rows="6" required></textarea>
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-paper-plane"></i> Gửi câu trả lời
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
    
    <c:if test="${isAdvisor}">
        <script>
            // Khởi tạo CKEditor cho textarea
            ClassicEditor
                .create(document.querySelector('#content'))
                .catch(error => {
                    console.error(error);
                });
        </script>
    </c:if>
</body>
</html> 