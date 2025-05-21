<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tìm kiếm nâng cao - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <h1 class="mb-4">Tìm kiếm nâng cao</h1>
        
        <!-- Form tìm kiếm -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/search">
                    <div class="form-row">
                        <div class="col-md-12 mb-3">
                            <div class="input-group">
                                <input type="text" class="form-control" name="keyword" value="${keyword}" 
                                       placeholder="Nhập từ khóa tìm kiếm...">
                                <div class="input-group-append">
                                    <button class="btn btn-primary" type="submit">
                                        <i class="fas fa-search"></i> Tìm kiếm
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="col-md-3 mb-3">
                            <label for="category">Loại câu hỏi</label>
                            <select class="form-control" id="category" name="category">
                                <option value="">Tất cả</option>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.id}" ${category.id == selectedCategory ? 'selected' : ''}>
                                        ${category.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="col-md-3 mb-3">
                            <label for="department">Khoa</label>
                            <select class="form-control" id="department" name="department">
                                <option value="">Tất cả</option>
                                <c:forEach items="${departments}" var="department">
                                    <option value="${department.id}" ${department.id == selectedDepartment ? 'selected' : ''}>
                                        ${department.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="col-md-3 mb-3">
                            <label for="major">Ngành học</label>
                            <select class="form-control" id="major" name="major">
                                <option value="">Tất cả</option>
                                <c:forEach items="${majors}" var="major">
                                    <option value="${major.id}" ${major.id == selectedMajor ? 'selected' : ''}>
                                        ${major.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="col-md-3 mb-3">
                            <label for="status">Trạng thái</label>
                            <select class="form-control" id="status" name="status">
                                <option value="">Tất cả</option>
                                <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>Đang chờ</option>
                                <option value="answered" ${selectedStatus == 'answered' ? 'selected' : ''}>Đã trả lời</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="col-md-3 mb-3">
                            <label for="sortBy">Sắp xếp theo</label>
                            <select class="form-control" id="sortBy" name="sortBy">
                                <option value="newest" ${sortBy == 'newest' ? 'selected' : ''}>Mới nhất</option>
                                <option value="oldest" ${sortBy == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                                <option value="popular" ${sortBy == 'popular' ? 'selected' : ''}>Phổ biến nhất</option>
                            </select>
                        </div>
                        
                        <div class="col-md-9 text-right align-self-end mb-3">
                            <button type="reset" class="btn btn-secondary">
                                <i class="fas fa-redo"></i> Đặt lại
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Kết quả tìm kiếm -->
        <c:if test="${not empty searchResults}">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Kết quả tìm kiếm (${resultCount})</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty searchResults}">
                            <div class="alert alert-info">
                                Không tìm thấy kết quả nào phù hợp với điều kiện tìm kiếm.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="list-group">
                                <c:forEach items="${searchResults}" var="question">
                                    <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" 
                                       class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">${question.title}</h5>
                                            <small class="text-muted">
                                                <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                        <p class="mb-1">${question.content}</p>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <small class="text-muted">
                                                Bởi: ${question.user.fullName} |
                                                <c:if test="${not empty question.category}">
                                                    Loại: ${question.category.name} |
                                                </c:if>
                                                <c:if test="${not empty question.major}">
                                                    Ngành: ${question.major.name} |
                                                </c:if>
                                                ${question.viewCount} lượt xem |
                                                ${question.answers.size()} câu trả lời
                                            </small>
                                            <span class="badge ${question.status == 'answered' ? 'badge-success' : 'badge-warning'}">
                                                ${question.status == 'answered' ? 'Đã trả lời' : 'Đang chờ'}
                                            </span>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
    
    <script>
        // Lọc ngành học theo khoa khi thay đổi khoa
        $(document).ready(function() {
            $('#department').change(function() {
                const departmentId = $(this).val();
                const majorSelect = $('#major');
                
                // Lưu giá trị đã chọn
                const selectedMajor = majorSelect.val();
                
                // Ẩn tất cả các ngành
                majorSelect.find('option').not(':first').hide();
                
                if (departmentId) {
                    // Hiển thị các ngành thuộc khoa đã chọn
                    majorSelect.find('option[data-department="' + departmentId + '"]').show();
                } else {
                    // Hiển thị tất cả các ngành nếu không chọn khoa
                    majorSelect.find('option').show();
                }
                
                // Nếu ngành đã chọn không thuộc khoa mới, đặt lại về mặc định
                if (selectedMajor && !majorSelect.find('option[value="' + selectedMajor + '"]:visible').length) {
                    majorSelect.val('');
                }
            });
            
            // Kích hoạt sự kiện change để áp dụng bộ lọc ban đầu
            $('#department').trigger('change');
        });
    </script>
</body>
</html> 
