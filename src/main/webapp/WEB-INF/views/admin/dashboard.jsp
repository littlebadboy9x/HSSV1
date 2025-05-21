<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Dashboard Admin</h1>
            <div>
                <a href="${pageContext.request.contextPath}/admin/statistics" class="btn btn-info">
                    <i class="fas fa-chart-bar"></i> Thống kê chi tiết
                </a>
            </div>
        </div>
        
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Tổng người dùng</h6>
                                <h2 class="mb-0">${totalUsers}</h2>
                            </div>
                            <i class="fas fa-users fa-3x"></i>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-top-0">
                        <a href="${pageContext.request.contextPath}/admin/users" class="text-white">
                            <i class="fas fa-arrow-right"></i> Quản lý người dùng
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card text-white bg-success mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Tổng câu hỏi</h6>
                                <h2 class="mb-0">${totalQuestions}</h2>
                            </div>
                            <i class="fas fa-question-circle fa-3x"></i>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-top-0">
                        <a href="${pageContext.request.contextPath}/admin/questions" class="text-white">
                            <i class="fas fa-arrow-right"></i> Quản lý câu hỏi
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card text-white bg-warning mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Câu hỏi đang chờ</h6>
                                <h2 class="mb-0">${pendingQuestions}</h2>
                            </div>
                            <i class="fas fa-clock fa-3x"></i>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-top-0">
                        <a href="${pageContext.request.contextPath}/admin/questions?status=PENDING" class="text-white">
                            <i class="fas fa-arrow-right"></i> Xem câu hỏi chờ
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-3">
                <div class="card text-white bg-info mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Tổng câu trả lời</h6>
                                <h2 class="mb-0">${totalAnswers}</h2>
                            </div>
                            <i class="fas fa-comments fa-3x"></i>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-top-0">
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=by_status" class="text-white">
                            <i class="fas fa-arrow-right"></i> Xem thống kê
                        </a>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="row">
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Quản lý hệ thống</h5>
                    </div>
                    <div class="list-group list-group-flush">
                        <a href="${pageContext.request.contextPath}/admin/users" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            <div>
                                <i class="fas fa-users mr-2"></i> Quản lý người dùng
                            </div>
                            <span class="badge badge-primary badge-pill">${totalUsers}</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/questions" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            <div>
                                <i class="fas fa-question-circle mr-2"></i> Quản lý câu hỏi
                            </div>
                            <span class="badge badge-primary badge-pill">${totalQuestions}</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action">
                            <i class="fas fa-folder mr-2"></i> Quản lý danh mục
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/majors" class="list-group-item list-group-item-action">
                            <i class="fas fa-graduation-cap mr-2"></i> Quản lý ngành học
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/departments" class="list-group-item list-group-item-action">
                            <i class="fas fa-building mr-2"></i> Quản lý khoa
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Báo cáo & Thống kê</h5>
                    </div>
                    <div class="list-group list-group-flush">
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=overview" class="list-group-item list-group-item-action">
                            <i class="fas fa-chart-pie mr-2"></i> Thống kê tổng quan
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=by_department" class="list-group-item list-group-item-action">
                            <i class="fas fa-chart-bar mr-2"></i> Thống kê theo khoa
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=by_major" class="list-group-item list-group-item-action">
                            <i class="fas fa-chart-line mr-2"></i> Thống kê theo ngành
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=by_category" class="list-group-item list-group-item-action">
                            <i class="fas fa-chart-area mr-2"></i> Thống kê theo danh mục
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/statistics?type=by_status" class="list-group-item list-group-item-action">
                            <i class="fas fa-tasks mr-2"></i> Thống kê theo trạng thái
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
