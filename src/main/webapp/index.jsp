<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Hệ thống Tư vấn Sinh viên</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/questions">Câu hỏi</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                               data-toggle="dropdown">
                                    ${sessionScope.user.fullName}
                            </a>
                            <div class="dropdown-menu dropdown-menu-right">
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
                                <c:if test="${sessionScope.isAdvisor}">
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin-panel">
                                        Quản trị
                                    </a>
                                </c:if>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                            </div>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

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
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Đặt câu hỏi</h5>
                    <p class="card-text">Đặt câu hỏi của bạn và nhận phản hồi từ chuyên gia.</p>
                    <a href="${pageContext.request.contextPath}/questions/ask" class="btn btn-primary">Đặt câu hỏi</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Tìm kiếm câu hỏi</h5>
                    <p class="card-text">Tìm kiếm câu hỏi đã có sẵn trong hệ thống.</p>
                    <a href="${pageContext.request.contextPath}/questions/search" class="btn btn-primary">Tìm kiếm</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">Câu hỏi của tôi</h5>
                    <p class="card-text">Xem lại các câu hỏi bạn đã đặt và trạng thái của chúng.</p>
                    <a href="${pageContext.request.contextPath}/questions/my-questions" class="btn btn-primary">Câu hỏi của tôi</a>
                </div>
            </div>
        </div>
    </div>
</div>

<footer class="bg-dark text-white mt-5 py-3">
    <div class="container text-center">
        <p>© 2024 Hệ thống Tư vấn Sinh viên. Bản quyền thuộc về HSSV.</p>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
