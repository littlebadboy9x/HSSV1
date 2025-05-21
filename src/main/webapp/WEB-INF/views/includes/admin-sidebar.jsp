<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="sidebar">
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-tachometer-alt mr-2"></i> Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/questions">
                <i class="fas fa-question-circle mr-2"></i> Quản lý câu hỏi
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                <i class="fas fa-users mr-2"></i> Quản lý người dùng
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">
                <i class="fas fa-list-alt mr-2"></i> Danh mục câu hỏi
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/majors">
                <i class="fas fa-graduation-cap mr-2"></i> Ngành học
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/departments">
                <i class="fas fa-building mr-2"></i> Khoa/Đơn vị
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/advisors">
                <i class="fas fa-user-tie mr-2"></i> Cố vấn
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/statistics">
                <i class="fas fa-chart-bar mr-2"></i> Thống kê
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/settings">
                <i class="fas fa-cog mr-2"></i> Cài đặt
            </a>
        </li>
    </ul>
</div>

<style>
    .sidebar {
        background-color: #343a40;
        color: #fff;
        height: 100%;
        min-height: calc(100vh - 56px);
        padding: 20px 0;
    }
    
    .sidebar .nav-link {
        color: rgba(255, 255, 255, 0.75);
        padding: 10px 20px;
        transition: all 0.2s;
    }
    
    .sidebar .nav-link:hover {
        color: #fff;
        background-color: rgba(255, 255, 255, 0.1);
    }
    
    .sidebar .nav-link.active {
        color: #fff;
        background-color: rgba(255, 255, 255, 0.2);
    }
</style>

<script>
    // Highlight current page in sidebar
    $(document).ready(function() {
        const currentUrl = window.location.pathname;
        $(".sidebar .nav-link").each(function() {
            const href = $(this).attr("href");
            if (currentUrl.includes(href) && href !== '${pageContext.request.contextPath}/') {
                $(this).addClass("active");
            }
        });
    });
</script> 