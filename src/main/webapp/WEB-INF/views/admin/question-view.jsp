<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- CKEditor 4 -->
    <script src="https://cdn.ckeditor.com/4.16.0/standard/ckeditor.js"></script>
    <style>
        .question-content img, .answer-content img {
            max-width: 100%;
            height: auto;
        }
        .status-badge {
            font-size: 0.85rem;
            padding: 0.4rem 0.7rem;
        }
        .question-meta {
            font-size: 0.9rem;
            color: #6c757d;
        }
        .answer-card {
            border-left: 4px solid #007bff;
        }
        .admin-answer {
            border-left: 4px solid #28a745;
        }
    </style>
</head>
<body>
    <jsp:include page="../includes/admin-header.jsp"/>
    
    <div class="container-fluid mt-4">
        <div class="row">
            <div class="col-md-2">
                <jsp:include page="../includes/admin-sidebar.jsp"/>
            </div>
            
            <div class="col-md-10">
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.successMessage}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <c:remove var="successMessage" scope="session"/>
                </c:if>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${errorMessage}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                
                <!-- Breadcrumb -->
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/questions">Quản lý câu hỏi</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chi tiết câu hỏi #${question.id}</li>
                    </ol>
                </nav>
                
                <!-- Thông tin câu hỏi -->
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fas fa-question-circle mr-2"></i>Chi tiết câu hỏi</h5>
                        <div>
                            <c:choose>
                                <c:when test="${question.status == 'PENDING'}">
                                    <span class="badge badge-warning status-badge">Chờ trả lời</span>
                                </c:when>
                                <c:when test="${question.status == 'ANSWERED'}">
                                    <span class="badge badge-success status-badge">Đã trả lời</span>
                                </c:when>
                                <c:when test="${question.status == 'CLOSED'}">
                                    <span class="badge badge-secondary status-badge">Đã đóng</span>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                    <div class="card-body">
                        <h4 class="card-title">${question.title}</h4>
                        <div class="question-meta mb-3">
                            <span><i class="far fa-user mr-1"></i>Người hỏi: 
                                <c:choose>
                                    <c:when test="${question.anonymous}">
                                        <em>Ẩn danh</em>
                                    </c:when>
                                    <c:otherwise>
                                        ${question.user.fullName}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                            <span class="ml-3"><i class="far fa-clock mr-1"></i>Ngày tạo: <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                            <span class="ml-3"><i class="far fa-folder mr-1"></i>Danh mục: ${question.category.name}</span>
                            <c:if test="${not empty question.major}">
                                <span class="ml-3"><i class="fas fa-graduation-cap mr-1"></i>Ngành học: ${question.major.name}</span>
                            </c:if>
                        </div>
                        <div class="question-content">
                            ${question.content}
                        </div>
                        
                        <div class="mt-4 d-flex">
                            <div class="btn-group" role="group">
                                <a href="${pageContext.request.contextPath}/admin/questions?action=edit&id=${question.id}" class="btn btn-primary">
                                    <i class="fas fa-edit mr-1"></i>Chỉnh sửa
                                </a>
                                <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#deleteModal">
                                    <i class="fas fa-trash mr-1"></i>Xóa câu hỏi
                                </button>
                            </div>
                            
                            <!-- Cập nhật trạng thái -->
                            <div class="dropdown ml-2">
                                <button class="btn btn-secondary dropdown-toggle" type="button" id="statusDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <i class="fas fa-cog mr-1"></i>Cập nhật trạng thái
                                </button>
                                <div class="dropdown-menu" aria-labelledby="statusDropdown">
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/questions?action=update-status&id=${question.id}&status=PENDING">Đặt thành Chờ trả lời</a>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/questions?action=update-status&id=${question.id}&status=ANSWERED">Đặt thành Đã trả lời</a>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/questions?action=update-status&id=${question.id}&status=CLOSED">Đặt thành Đã đóng</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Modal xác nhận xóa -->
                <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header bg-danger text-white">
                                <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <p>Bạn có chắc chắn muốn xóa câu hỏi "<strong>${question.title}</strong>"?</p>
                                <p class="text-danger"><strong>Lưu ý:</strong> Thao tác này không thể hoàn tác.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                                <a href="${pageContext.request.contextPath}/admin/questions?action=delete&id=${question.id}" class="btn btn-danger">Xóa</a>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Danh sách câu trả lời -->
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fas fa-comments mr-2"></i>Câu trả lời</h5>
                        <span class="badge badge-info">${answers.size()} câu trả lời</span>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty answers}">
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle mr-2"></i>Câu hỏi này chưa có câu trả lời nào.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${answers}" var="answer" varStatus="status">
                                    <div class="card mb-3 ${answer.user.advisorProfile != null ? 'admin-answer' : 'answer-card'}">
                                        <div class="card-header bg-light d-flex justify-content-between align-items-center">
                                            <div>
                                                <strong>${answer.user.fullName}</strong>
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
                    </div>
                </div>
                
                <!-- Form trả lời câu hỏi -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="far fa-paper-plane mr-2"></i>Trả lời câu hỏi</h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/questions" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="answer">
                            <input type="hidden" name="questionId" value="${question.id}">
                            
                            <div class="form-group">
                                <label for="content">Nội dung câu trả lời</label>
                                <textarea class="form-control" id="content" name="content" rows="5" required></textarea>
                            </div>
                            
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="setAnswered" name="setAnswered" value="true" checked>
                                <label class="form-check-label" for="setAnswered">Đánh dấu câu hỏi là "Đã trả lời"</label>
                            </div>
                            
                            <div class="text-right">
                                <button type="submit" class="btn btn-success">
                                    <i class="fas fa-paper-plane mr-1"></i>Gửi câu trả lời
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    <script>
        $(document).ready(function() {
            // Khởi tạo CKEditor
            CKEDITOR.replace('content', {
                language: 'vi',
                height: 300,
                removeButtons: 'Save,NewPage,Preview,Print,Templates,PasteFromWord'
            });
            
            // Tự động đóng các thông báo sau 5 giây
            setTimeout(function() {
                $(".alert").alert('close');
            }, 5000);
        });
    </script>
</body>
</html> 