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
    <!-- Thêm CKEditor 4 thay vì CKEditor 5 -->
    <script src="https://cdn.ckeditor.com/4.16.0/standard/ckeditor.js"></script>
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
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${sessionScope.errorMessage}
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success" role="alert">
                ${sessionScope.successMessage}
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        
        <!-- Chi tiết câu hỏi -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h2 class="mb-0">${question.title}</h2>
                <span class="badge ${question.status == 'ANSWERED' ? 'badge-answered' : 'badge-pending'}">
                    ${question.status == 'ANSWERED' ? 'Đã trả lời' : 'Đang chờ'}
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
                            Đăng bởi: ${question.anonymous ? 'Ẩn danh' : question.user.fullName} |
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
                
                <!-- Hiển thị nút chỉnh sửa nếu người dùng là chủ sở hữu và câu hỏi chưa được trả lời -->
                <c:if test="${isOwner && question.status != 'ANSWERED' && question.status != 'CLOSED'}">
                    <div class="mt-3">
                        <a href="${pageContext.request.contextPath}/questions/edit?id=${question.id}" class="btn btn-primary btn-sm">
                            <i class="fas fa-edit"></i> Chỉnh sửa câu hỏi
                        </a>
                    </div>
                </c:if>
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
                                <c:if test="${answer.user.advisorProfile != null}">
                                    <span class="badge badge-primary ml-1">Cố vấn</span>
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
                    <form method="post" action="${pageContext.request.contextPath}/questions/view">
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
            // Khởi tạo CKEditor 4 cho textarea
            CKEDITOR.replace('content', {
                language: 'vi',
                height: 300,
                removeButtons: 'Save,NewPage,Preview,Print,Templates,PasteFromWord'
            });
            
            // Đảm bảo dữ liệu CKEditor được gửi đi khi submit form
            document.querySelector('form').addEventListener('submit', function(e) {
                // Cập nhật nội dung từ CKEditor vào textarea trước khi submit
                for (var instanceName in CKEDITOR.instances) {
                    CKEDITOR.instances[instanceName].updateElement();
                }
            });
        </script>
    </c:if>
</body>
</html> 