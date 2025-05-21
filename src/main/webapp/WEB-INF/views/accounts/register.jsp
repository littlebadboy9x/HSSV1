<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Đăng ký tài khoản</h4>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                ${errorMessage}
                            </div>
                        </c:if>
                        
                        <form method="post" action="${pageContext.request.contextPath}/register">
                            <!-- Thông tin cơ bản -->
                            <div class="form-group">
                                <label for="username">Tên đăng nhập <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="username" name="username" 
                                    value="${username}" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="email">Email <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="email" name="email" 
                                    value="${email}" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="fullName">Họ và tên <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="fullName" name="fullName" 
                                    value="${fullName}" required>
                            </div>

                            <div class="form-group">
                                <label for="password">Mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="password" name="password" 
                                    required minlength="6">
                                <small class="form-text text-muted">Mật khẩu phải có ít nhất 6 ký tự.</small>
                            </div>
                            
                            <div class="form-group">
                                <label for="confirmPassword">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="confirmPassword" 
                                    name="confirmPassword" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="userType">Loại tài khoản <span class="text-danger">*</span></label>
                                <select class="form-control" id="userType" name="userType" required>
                                    <option value="">-- Chọn loại tài khoản --</option>
                                    <option value="STUDENT" ${userType == 'STUDENT' ? 'selected' : ''}>Sinh viên</option>
                                    <option value="ALUMNI" ${userType == 'ALUMNI' ? 'selected' : ''}>Cựu sinh viên</option>
                                    <option value="PARENT" ${userType == 'PARENT' ? 'selected' : ''}>Phụ huynh</option>
                                    <option value="HIGH_SCHOOL_STUDENT" ${userType == 'HIGH_SCHOOL_STUDENT' ? 'selected' : ''}>
                                        Học sinh phổ thông
                                    </option>
                                </select>
                            </div>
                            
                            <!-- Các trường bổ sung cho từng loại tài khoản -->
                            <div id="studentFields" class="additional-fields">
                                <div class="form-group">
                                    <label for="studentId">Mã số sinh viên <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="studentId" name="studentId" 
                                        value="${studentId}">
                                </div>
                            </div>
                            
                            <div id="alumniFields" class="additional-fields" style="display:none;">
                                <div class="form-group">
                                    <label for="schoolYear">Niên khóa <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="schoolYear" name="schoolYear" 
                                        value="${schoolYear}" placeholder="Ví dụ: 2015-2019">
                                </div>
                            </div>
                            
                            <div id="parentFields" class="additional-fields" style="display:none;">
                                <div class="form-group">
                                    <label for="phoneNumber">Số điện thoại <span class="text-danger">*</span></label>
                                    <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" 
                                        value="${phoneNumber}">
                                </div>
                            </div>
                            
                            <div id="highSchoolStudentFields" class="additional-fields" style="display:none;">
                                <div class="form-group">
                                    <label for="className">Lớp <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="className" name="className" 
                                        value="${className}" placeholder="Ví dụ: 12A1">
                                </div>
                            </div>
                            
                            <div class="form-group text-center mt-4">
                                <button type="submit" class="btn btn-primary px-4">Đăng ký</button>
                            </div>
                            
                            <div class="text-center mt-3">
                                <p>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp" />
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    <script>
    $(document).ready(function() {
        // Xử lý hiển thị các trường bổ sung theo loại tài khoản
        $('#userType').change(function() {
            // Ẩn tất cả các trường bổ sung
            $('.additional-fields').hide();
            
            // Hiển thị các trường tương ứng với loại tài khoản được chọn
            var userType = $(this).val();
            switch(userType) {
                case 'STUDENT':
                    $('#studentFields').show();
                    break;
                case 'ALUMNI':
                    $('#alumniFields').show();
                    break;
                case 'PARENT':
                    $('#parentFields').show();
                    break;
                case 'HIGH_SCHOOL_STUDENT':
                    $('#highSchoolStudentFields').show();
                    break;
            }
        });
        
        // Kích hoạt sự kiện change để hiển thị đúng trường khi tải lại trang
        $('#userType').trigger('change');
    });
    </script>
</body>
</html> 