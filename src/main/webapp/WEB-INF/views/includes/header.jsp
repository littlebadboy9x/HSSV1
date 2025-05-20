<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">HSSV</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/questions">Câu hỏi</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/questions/ask">Đặt câu hỏi</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/search">Tìm kiếm</a>
                </li>
                <c:if test="${sessionScope.isAdvisor}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="adminDropdown" role="button"
                           data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            Quản lý
                        </a>
                        <div class="dropdown-menu" aria-labelledby="adminDropdown">
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/questions">
                                Quản lý câu hỏi
                            </a>
                            <c:if test="${sessionScope.isAdmin}">
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                    Quản lý người dùng
                                </a>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/statistics">
                                    Thống kê
                                </a>
                            </c:if>
                        </div>
                    </li>
                </c:if>
            </ul>
            <form class="form-inline my-2 my-lg-0 mr-3" action="${pageContext.request.contextPath}/search" method="get">
                <div class="input-group">
                    <input class="form-control" type="search" name="keyword" placeholder="Tìm kiếm..." aria-label="Search">
                    <div class="input-group-append">
                        <button class="btn btn-outline-light" type="submit">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
            </form>
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
                               data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                ${sessionScope.user.fullName}
                            </a>
                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                    <i class="fas fa-user"></i> Thông tin cá nhân
                                </a>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/questions/my-questions">
                                    <i class="fas fa-question-circle"></i> Câu hỏi của tôi
                                </a>
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a>
                            </div>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<c:if test="${not empty param.message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.message eq 'login_success'}">
                Đăng nhập thành công!
            </c:when>
            <c:when test="${param.message eq 'logout_success'}">
                Đăng xuất thành công!
            </c:when>
            <c:when test="${param.message eq 'register_success'}">
                Đăng ký thành công! Vui lòng đăng nhập.
            </c:when>
            <c:otherwise>
                ${param.message}
            </c:otherwise>
        </c:choose>
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${sessionScope.errorMessage}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
    <c:remove var="errorMessage" scope="session" />
</c:if> 