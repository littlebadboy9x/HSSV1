<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Khoa/Đơn vị - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Quản lý Khoa/Đơn vị</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/departments?action=create" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Thêm khoa/đơn vị mới
                </a>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Quay lại Dashboard
                </a>
            </div>
        </div>
        
        <c:if test="${param.message != null}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <c:choose>
                    <c:when test="${param.message == 'create_success'}">
                        <strong>Thành công!</strong> Khoa/Đơn vị mới đã được tạo.
                    </c:when>
                    <c:when test="${param.message == 'update_success'}">
                        <strong>Thành công!</strong> Khoa/Đơn vị đã được cập nhật.
                    </c:when>
                    <c:when test="${param.message == 'delete_success'}">
                        <strong>Thành công!</strong> Khoa/Đơn vị đã được xóa.
                    </c:when>
                    <c:otherwise>
                        <strong>Thành công!</strong> Thao tác đã được thực hiện.
                    </c:otherwise>
                </c:choose>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        
        <c:if test="${param.error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <c:choose>
                    <c:when test="${param.error == 'delete_failed' && param.info == 'department_has_majors_or_advisors'}">
                        <strong>Lỗi!</strong> Không thể xóa khoa/đơn vị vì vẫn có ngành học hoặc cố vấn liên kết.
                    </c:when>
                    <c:when test="${param.error == 'delete_failed'}">
                        <strong>Lỗi!</strong> Không thể xóa khoa/đơn vị.
                    </c:when>
                    <c:otherwise>
                        <strong>Lỗi!</strong> Đã xảy ra lỗi khi thực hiện thao tác.
                    </c:otherwise>
                </c:choose>
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        
        <c:if test="${errorMessage != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong>Lỗi!</strong> ${errorMessage}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-header bg-light">
                <h5 class="card-title mb-0">Danh sách khoa/đơn vị</h5>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-striped table-hover mb-0">
                        <thead class="thead-light">
                            <tr>
                                <th scope="col" style="width: 5%">#</th>
                                <th scope="col" style="width: 25%">Tên khoa/đơn vị</th>
                                <th scope="col" style="width: 15%">Mã khoa</th>
                                <th scope="col" style="width: 35%">Mô tả</th>
                                <th scope="col" style="width: 20%">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty departments}">
                                    <tr>
                                        <td colspan="5" class="text-center">Không có khoa/đơn vị nào.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="department" items="${departments}" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>${department.name}</td>
                                            <td>${department.code}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty department.description}">
                                                        <em class="text-muted">Không có mô tả</em>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${department.description}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <a href="${pageContext.request.contextPath}/admin/departments?action=edit&id=${department.id}" 
                                                       class="btn btn-primary mr-1" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-danger" 
                                                            data-toggle="modal" 
                                                            data-target="#deleteModal${department.id}" 
                                                            title="Xóa">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                    
                                                    <!-- Modal xác nhận xóa -->
                                                    <div class="modal fade" id="deleteModal${department.id}" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel${department.id}" aria-hidden="true">
                                                        <div class="modal-dialog" role="document">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title" id="deleteModalLabel${department.id}">Xác nhận xóa</h5>
                                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                        <span aria-hidden="true">&times;</span>
                                                                    </button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    Bạn có chắc chắn muốn xóa khoa/đơn vị <strong>${department.name}</strong>?
                                                                    <p class="text-danger mt-2 mb-0">
                                                                        <i class="fas fa-exclamation-triangle"></i> 
                                                                        Lưu ý: Thao tác này sẽ không thành công nếu khoa/đơn vị có ngành học hoặc cố vấn liên kết.
                                                                    </p>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                                                                    <a href="${pageContext.request.contextPath}/admin/departments?action=delete&id=${department.id}" class="btn btn-danger">Xóa</a>
                                                                </div>
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
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 