<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Câu hỏi của tôi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
    <div class="container mt-4">
        <h2>Câu hỏi của tôi</h2>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/questions/ask" class="btn btn-primary">
                <i class="fas fa-plus"></i> Đặt câu hỏi mới
            </a>
        </div>
        
        <c:choose>
            <c:when test="${empty questions}">
                <div class="alert alert-info">
                    Bạn chưa đặt câu hỏi nào. Hãy nhấn vào nút "Đặt câu hỏi mới" để bắt đầu!
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="thead-light">
                            <tr>
                                <th>Tiêu đề</th>
                                <th>Danh mục</th>
                                <th>Ngày đăng</th>
                                <th>Trạng thái</th>
                                <th>Lượt xem</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${questions}" var="question">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/questions/view?id=${question.id}">
                                            ${question.title}
                                        </a>
                                    </td>
                                    <td>${question.category.name}</td>
                                    <td>
                                        <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${question.status eq 'PENDING'}">
                                                <span class="badge badge-warning">Chờ trả lời</span>
                                            </c:when>
                                            <c:when test="${question.status eq 'ANSWERED'}">
                                                <span class="badge badge-success">Đã trả lời</span>
                                            </c:when>
                                            <c:when test="${question.status eq 'CLOSED'}">
                                                <span class="badge badge-secondary">Đã đóng</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>${question.viewCount}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/questions/view?id=${question.id}" 
                                               class="btn btn-info" title="Xem">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${question.status eq 'PENDING'}">
                                                <a href="${pageContext.request.contextPath}/questions/edit?id=${question.id}" 
                                                   class="btn btn-primary" title="Sửa">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="#" onclick="confirmDelete(${question.id})" 
                                                   class="btn btn-danger" title="Xóa">
                                                    <i class="fas fa-trash-alt"></i>
                                                </a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="../includes/footer.jsp" />
    
    <!-- Modal xác nhận xóa -->
    <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác nhận xóa</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    Bạn có chắc chắn muốn xóa câu hỏi này không? Hành động này không thể hoàn tác.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                    <a href="#" id="confirmDeleteButton" class="btn btn-danger">Xóa</a>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    <script>
        function confirmDelete(questionId) {
            document.getElementById('confirmDeleteButton').href = 
                '${pageContext.request.contextPath}/questions/delete?id=' + questionId;
            $('#deleteModal').modal('show');
        }
    </script>
</body>
</html> 
