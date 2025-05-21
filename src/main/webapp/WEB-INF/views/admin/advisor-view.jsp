<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin Cố vấn - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Thông tin Chi tiết Cố vấn</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/advisors?action=edit&id=${advisor.id}" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Chỉnh sửa
                </a>
                <a href="${pageContext.request.contextPath}/admin/advisors" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Quay lại danh sách
                </a>
            </div>
        </div>
        
        <c:if test="${advisor != null}">
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">Thông tin cơ bản</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3 text-center mb-3">
                            <div class="border rounded p-3 mb-2">
                                <i class="fas fa-user-tie fa-5x text-primary mb-3"></i>
                                <h5>${advisor.user.fullName}</h5>
                                <p class="text-muted mb-0">${advisor.title}</p>
                            </div>
                            <c:choose>
                                <c:when test="${advisor.available}">
                                    <span class="badge badge-success p-2"><i class="fas fa-check-circle"></i> Đang hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-secondary p-2"><i class="fas fa-pause-circle"></i> Tạm ngưng</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-9">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Họ và tên:</h6>
                                    <p>${advisor.user.fullName}</p>
                                </div>
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Email:</h6>
                                    <p>${advisor.user.email}</p>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Vai trò:</h6>
                                    <p>
                                        <c:choose>
                                            <c:when test="${advisor.role == 'ADMIN'}">
                                                <span class="badge badge-danger p-2">Admin</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-info p-2">Cố vấn</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Số điện thoại:</h6>
                                    <p>${not empty advisor.phone ? advisor.phone : '<em class="text-muted">Chưa cung cấp</em>'}</p>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Khoa/Đơn vị:</h6>
                                    <p>${advisor.department.name}</p>
                                </div>
                                <div class="col-md-6">
                                    <h6 class="font-weight-bold">Ngành học:</h6>
                                    <p>${advisor.major != null ? advisor.major.name : '<em class="text-muted">Không có</em>'}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="card mb-4">
                <div class="card-header bg-light">
                    <h5 class="card-title mb-0">Thông tin chi tiết</h5>
                </div>
                <div class="card-body">
                    <h6 class="font-weight-bold">Chức danh:</h6>
                    <p>${advisor.title}</p>
                    
                    <h6 class="font-weight-bold">Lĩnh vực chuyên môn:</h6>
                    <p>${not empty advisor.expertise ? advisor.expertise : '<em class="text-muted">Chưa cung cấp</em>'}</p>
                    
                    <h6 class="font-weight-bold">Giới thiệu:</h6>
                    <p>${not empty advisor.bio ? advisor.bio : '<em class="text-muted">Chưa cung cấp</em>'}</p>
                </div>
            </div>
            
            <div class="text-right">
                <a href="${pageContext.request.contextPath}/admin/advisors?action=edit&id=${advisor.id}" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Chỉnh sửa
                </a>
                <button class="btn btn-danger" data-toggle="modal" data-target="#deleteModal">
                    <i class="fas fa-trash-alt"></i> Xóa
                </button>
            </div>
            
            <!-- Modal xác nhận xóa -->
            <div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="deleteModalLabel">Xác nhận xóa</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            Bạn có chắc chắn muốn xóa cố vấn <strong>${advisor.user.fullName}</strong>?
                            <p class="text-danger mt-2 mb-0">
                                <i class="fas fa-exclamation-triangle"></i> 
                                Lưu ý: Thao tác này sẽ xóa thông tin hồ sơ cố vấn, nhưng không xóa tài khoản người dùng.
                            </p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                            <a href="${pageContext.request.contextPath}/admin/advisors?action=delete&id=${advisor.id}" class="btn btn-danger">Xóa</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
        
        <c:if test="${advisor == null}">
            <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle"></i> Không tìm thấy thông tin cố vấn với ID được cung cấp.
                <br>
                <a href="${pageContext.request.contextPath}/admin/advisors" class="btn btn-sm btn-warning mt-2">
                    Quay lại danh sách cố vấn
                </a>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 