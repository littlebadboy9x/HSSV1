<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-graduation-cap mr-2"></i>
            <span class="font-weight-bold">Hệ thống Tư vấn Sinh viên</span>
            <span class="badge badge-light ml-2">Admin</span>
        </a>
        
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarAdmin" aria-controls="navbarAdmin" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarAdmin">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/" target="_blank">
                        <i class="fas fa-external-link-alt mr-1"></i> Xem trang chính
                    </a>
                </li>
                
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="notificationDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fas fa-bell mr-1"></i> Thông báo
                        <c:if test="${notificationCount > 0}">
                            <span class="badge badge-danger">${notificationCount}</span>
                        </c:if>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="notificationDropdown">
                        <c:choose>
                            <c:when test="${empty notifications}">
                                <span class="dropdown-item-text">Không có thông báo mới</span>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${notifications}" var="notification" end="4">
                                    <a class="dropdown-item" href="${notification.link}">
                                        <small class="text-muted">${notification.time}</small>
                                        <p class="mb-0">${notification.message}</p>
                                    </a>
                                    <div class="dropdown-divider"></div>
                                </c:forEach>
                                <a class="dropdown-item text-center" href="${pageContext.request.contextPath}/admin/notifications">
                                    <i class="fas fa-arrow-right mr-1"></i> Xem tất cả
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </li>
                
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fas fa-user-circle mr-1"></i> ${sessionScope.user.fullName}
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/profile">
                            <i class="fas fa-user mr-2"></i> Hồ sơ
                        </a>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/settings">
                            <i class="fas fa-cog mr-2"></i> Cài đặt
                        </a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt mr-2"></i> Đăng xuất
                        </a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>

<style>
    .navbar-brand {
        font-size: 1.2rem;
    }
    
    .dropdown-menu {
        padding: 0.5rem 0;
    }
    
    .dropdown-item {
        padding: 0.5rem 1.5rem;
    }
    
    .dropdown-item-text {
        padding: 0.5rem 1.5rem;
        color: #6c757d;
    }
</style> 
