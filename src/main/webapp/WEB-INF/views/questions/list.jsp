<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col-md-8">
                <h2>Danh sách câu hỏi</h2>
            </div>
            <div class="col-md-4 text-right">
                <a href="${pageContext.request.contextPath}/questions/ask" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Đặt câu hỏi
                </a>
            </div>
        </div>
        
        <!-- Bộ lọc -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/questions" class="row">
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="category">Loại câu hỏi</label>
                            <select name="category" id="category" class="form-control">
                                <option value="">Tất cả</option>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.id}" ${category.id == selectedCategory ? 'selected' : ''}>
                                        ${category.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="major">Ngành học</label>
                            <select name="major" id="major" class="form-control">
                                <option value="">Tất cả</option>
                                <c:forEach items="${majors}" var="major">
                                    <option value="${major.id}" ${major.id == selectedMajor ? 'selected' : ''}>
                                        ${major.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="status">Trạng thái</label>
                            <select name="status" id="status" class="form-control">
                                <option value="">Tất cả</option>
                                <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>
                                    Đang chờ
                                </option>
                                <option value="answered" ${selectedStatus == 'answered' ? 'selected' : ''}>
                                    Đã trả lời
                                </option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="keyword">Tìm kiếm</label>
                            <input type="text" name="keyword" id="keyword" class="form-control" 
                                   value="${keyword}" placeholder="Nhập từ khóa...">
                        </div>
                    </div>
                    <div class="col-12 text-right">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-search"></i> Tìm kiếm
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Danh sách câu hỏi -->
        <div class="list-group">
            <c:choose>
                <c:when test="${empty questions}">
                    <div class="alert alert-info">
                        Không tìm thấy câu hỏi nào phù hợp với điều kiện tìm kiếm.
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${questions}" var="question">
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
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
</body>
</html> 