<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
    <jsp:include page="includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="jumbotron">
            <h1 class="display-4">Chào mừng đến với Hệ thống Tư vấn Sinh viên</h1>
            <p class="lead">Nền tảng hỗ trợ sinh viên, phụ huynh, cựu sinh viên và học sinh phổ thông trong việc giải đáp thắc mắc về giáo dục.</p>
            <hr class="my-4">
            <p>Nếu bạn có câu hỏi, hãy đăng nhập và đặt câu hỏi cho đội ngũ tư vấn của chúng tôi.</p>
            <a class="btn btn-primary btn-lg" href="${pageContext.request.contextPath}/questions" role="button">Xem câu hỏi</a>
            <c:if test="${empty sessionScope.user}">
                <a class="btn btn-outline-primary btn-lg" href="${pageContext.request.contextPath}/register" role="button">Đăng ký ngay</a>
            </c:if>
        </div>

        <div class="row">
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Câu hỏi mới được trả lời</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty recentAnsweredQuestions}">
                            <p class="text-muted">Chưa có câu hỏi nào được trả lời.</p>
                        </c:if>
                        <c:if test="${not empty recentAnsweredQuestions}">
                            <div class="list-group">
                                <c:forEach items="${recentAnsweredQuestions}" var="question">
                                    <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">${question.title}</h6>
                                            <small><fmt:formatDate value="${question.updatedAt}" pattern="dd/MM/yyyy" /></small>
                                        </div>
                                        <small class="text-muted">Bởi: ${question.user.fullName}</small>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">Câu hỏi phổ biến</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty popularQuestions}">
                            <p class="text-muted">Chưa có câu hỏi phổ biến.</p>
                        </c:if>
                        <c:if test="${not empty popularQuestions}">
                            <div class="list-group">
                                <c:forEach items="${popularQuestions}" var="question">
                                    <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h6 class="mb-1">${question.title}</h6>
                                            <small>${question.viewCount} lượt xem</small>
                                        </div>
                                        <small class="text-muted">Bởi: ${question.user.fullName}</small>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="row mt-4">
            <div class="col-md-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <h5 class="card-title">Đặt câu hỏi</h5>
                        <p class="card-text">Đặt câu hỏi của bạn và nhận phản hồi từ chuyên gia.</p>
                        <a href="${pageContext.request.contextPath}/questions/ask" class="btn btn-primary">Đặt câu hỏi</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tìm kiếm câu hỏi</h5>
                        <p class="card-text">Tìm kiếm câu hỏi đã có sẵn trong hệ thống.</p>
                        <a href="${pageContext.request.contextPath}/questions/search" class="btn btn-primary">Tìm kiếm</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card mb-4">
                    <div class="card-body text-center">
                        <h5 class="card-title">Câu hỏi của tôi</h5>
                        <p class="card-text">Xem lại các câu hỏi bạn đã đặt và trạng thái của chúng.</p>
                        <a href="${pageContext.request.contextPath}/questions/my-questions" class="btn btn-primary">Câu hỏi của tôi</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html> 