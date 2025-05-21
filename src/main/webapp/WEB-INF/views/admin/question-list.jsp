<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <style>
        .status-badge {
            font-size: 0.8rem;
            padding: 0.3rem 0.6rem;
        }
        .table th {
            background-color: #f8f9fa;
        }
        .admin-header {
            background-color: #343a40;
            color: white;
            padding: 1rem;
            margin-bottom: 1.5rem;
            border-radius: 5px;
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
                <div class="card">
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fas fa-question-circle mr-2"></i>Quản lý câu hỏi</h5>
                        <span class="badge badge-light">${questions.size()} câu hỏi</span>
                    </div>
                    
                    <div class="card-body">
                        <c:if test="${not empty sessionScope.successMessage}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                ${sessionScope.successMessage}
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <c:remove var="successMessage" scope="session"/>
                        </c:if>
                        
                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${sessionScope.errorMessage}
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <c:remove var="errorMessage" scope="session"/>
                        </c:if>
                        
                        <!-- Bộ lọc -->
                        <div class="card mb-4">
                            <div class="card-header bg-light">
                                <h6 class="mb-0"><i class="fas fa-filter mr-2"></i>Bộ lọc câu hỏi</h6>
                            </div>
                            <div class="card-body">
                                <form method="get" action="${pageContext.request.contextPath}/admin/questions" class="row">
                                    <div class="col-md-4 mb-3">
                                        <label for="status">Trạng thái:</label>
                                        <select name="status" id="status" class="form-control">
                                            <option value="">Tất cả trạng thái</option>
                                            <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Chờ trả lời</option>
                                            <option value="ANSWERED" ${param.status == 'ANSWERED' ? 'selected' : ''}>Đã trả lời</option>
                                            <option value="CLOSED" ${param.status == 'CLOSED' ? 'selected' : ''}>Đã đóng</option>
                                        </select>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="categoryId">Danh mục:</label>
                                        <select name="categoryId" id="categoryId" class="form-control">
                                            <option value="">Tất cả danh mục</option>
                                            <c:forEach items="${categories}" var="category">
                                                <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="majorId">Ngành học:</label>
                                        <select name="majorId" id="majorId" class="form-control">
                                            <option value="">Tất cả ngành học</option>
                                            <c:forEach items="${majors}" var="major">
                                                <option value="${major.id}" ${param.majorId == major.id ? 'selected' : ''}>${major.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-search mr-1"></i>Lọc
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/questions" class="btn btn-secondary ml-2">
                                            <i class="fas fa-redo mr-1"></i>Đặt lại
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                        
                        <!-- Danh sách câu hỏi -->
                        <div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Tiêu đề</th>
                                        <th>Người hỏi</th>
                                        <th>Danh mục</th>
                                        <th>Ngày tạo</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty questions}">
                                            <tr>
                                                <td colspan="7" class="text-center">Không có câu hỏi nào.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach items="${questions}" var="question">
                                                <tr>
                                                    <td>${question.id}</td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/admin/questions?action=view&id=${question.id}">
                                                            ${question.title}
                                                        </a>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${question.anonymous}">
                                                                <em>Ẩn danh</em>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${question.user.fullName}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${question.category.name}</td>
                                                    <td><fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                    <td>
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
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm" role="group">
                                                            <a href="${pageContext.request.contextPath}/admin/questions?action=view&id=${question.id}" class="btn btn-info" title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/admin/questions?action=edit&id=${question.id}" class="btn btn-primary" title="Chỉnh sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#deleteModal${question.id}" title="Xóa">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </div>
                                                        
                                                        <!-- Modal xác nhận xóa -->
                                                        <div class="modal fade" id="deleteModal${question.id}" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel${question.id}" aria-hidden="true">
                                                            <div class="modal-dialog" role="document">
                                                                <div class="modal-content">
                                                                    <div class="modal-header bg-danger text-white">
                                                                        <h5 class="modal-title" id="deleteModalLabel${question.id}">Xác nhận xóa</h5>
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
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
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
            // Tự động đóng các thông báo sau 5 giây
            setTimeout(function() {
                $(".alert").alert('close');
            }, 5000);
        });
    </script>
</body>
</html> 
