<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- Thêm Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <h1 class="mb-4">Thống kê hệ thống</h1>
        
        <!-- Tabs thống kê -->
        <ul class="nav nav-tabs mb-4">
            <li class="nav-item">
                <a class="nav-link ${statisticsType == 'overview' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/statistics?type=overview">
                    Tổng quan
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${statisticsType == 'by_department' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/statistics?type=by_department">
                    Theo khoa
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${statisticsType == 'by_major' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/statistics?type=by_major">
                    Theo ngành
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${statisticsType == 'by_category' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/statistics?type=by_category">
                    Theo loại câu hỏi
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${statisticsType == 'by_status' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/admin/statistics?type=by_status">
                    Theo trạng thái
                </a>
            </li>
        </ul>
        
        <!-- Nội dung thống kê -->
        <div class="tab-content">
            <!-- Thống kê tổng quan -->
            <c:if test="${statisticsType == 'overview'}">
                <div class="row">
                    <div class="col-md-3">
                        <div class="card text-white bg-primary mb-3">
                            <div class="card-header">Tổng số câu hỏi</div>
                            <div class="card-body">
                                <h1 class="card-title">${totalQuestions}</h1>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-white bg-success mb-3">
                            <div class="card-header">Đã trả lời</div>
                            <div class="card-body">
                                <h1 class="card-title">${answeredQuestions}</h1>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-white bg-warning mb-3">
                            <div class="card-header">Đang chờ</div>
                            <div class="card-body">
                                <h1 class="card-title">${pendingQuestions}</h1>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-white bg-info mb-3">
                            <div class="card-header">Tỷ lệ trả lời</div>
                            <div class="card-body">
                                <h1 class="card-title"><fmt:formatNumber value="${responseRate}" pattern="0.0" />%</h1>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">Tỷ lệ câu hỏi theo trạng thái</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="statusChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                
                <script>
                    // Biểu đồ trạng thái
                    const statusCtx = document.getElementById('statusChart').getContext('2d');
                    const statusChart = new Chart(statusCtx, {
                        type: 'pie',
                        data: {
                            labels: ['Đã trả lời', 'Đang chờ'],
                            datasets: [{
                                data: [${answeredQuestions}, ${pendingQuestions}],
                                backgroundColor: ['#28a745', '#ffc107']
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                }
                            }
                        }
                    });
                </script>
            </c:if>
            
            <!-- Thống kê theo khoa -->
            <c:if test="${statisticsType == 'by_department'}">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Số lượng câu hỏi theo khoa</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="departmentChart"></canvas>
                    </div>
                </div>
                
                <div class="table-responsive mt-4">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Khoa</th>
                                <th>Số câu hỏi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${departmentStats}" var="entry">
                                <tr>
                                    <td>${entry.key.name}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <script>
                    // Biểu đồ theo khoa
                    const departmentCtx = document.getElementById('departmentChart').getContext('2d');
                    const departmentChart = new Chart(departmentCtx, {
                        type: 'bar',
                        data: {
                            labels: [
                                <c:forEach items="${departmentStats}" var="entry" varStatus="status">
                                    '${entry.key.name}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ],
                            datasets: [{
                                label: 'Số câu hỏi',
                                data: [
                                    <c:forEach items="${departmentStats}" var="entry" varStatus="status">
                                        ${entry.value}${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                                borderColor: 'rgba(54, 162, 235, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        precision: 0
                                    }
                                }
                            }
                        }
                    });
                </script>
            </c:if>
            
            <!-- Thống kê theo ngành -->
            <c:if test="${statisticsType == 'by_major'}">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Số lượng câu hỏi theo ngành</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="majorChart"></canvas>
                    </div>
                </div>
                
                <div class="table-responsive mt-4">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Ngành</th>
                                <th>Khoa</th>
                                <th>Số câu hỏi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${majorStats}" var="entry">
                                <tr>
                                    <td>${entry.key.name}</td>
                                    <td>${entry.key.department.name}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <script>
                    // Biểu đồ theo ngành
                    const majorCtx = document.getElementById('majorChart').getContext('2d');
                    const majorChart = new Chart(majorCtx, {
                        type: 'bar',
                        data: {
                            labels: [
                                <c:forEach items="${majorStats}" var="entry" varStatus="status">
                                    '${entry.key.name}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ],
                            datasets: [{
                                label: 'Số câu hỏi',
                                data: [
                                    <c:forEach items="${majorStats}" var="entry" varStatus="status">
                                        ${entry.value}${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                backgroundColor: 'rgba(75, 192, 192, 0.5)',
                                borderColor: 'rgba(75, 192, 192, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        precision: 0
                                    }
                                }
                            }
                        }
                    });
                </script>
            </c:if>
            
            <!-- Thống kê theo loại câu hỏi -->
            <c:if test="${statisticsType == 'by_category'}">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Số lượng câu hỏi theo loại</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="categoryChart"></canvas>
                    </div>
                </div>
                
                <div class="table-responsive mt-4">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Loại câu hỏi</th>
                                <th>Số câu hỏi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${categoryStats}" var="entry">
                                <tr>
                                    <td>${entry.key.name}</td>
                                    <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <script>
                    // Biểu đồ theo loại câu hỏi
                    const categoryCtx = document.getElementById('categoryChart').getContext('2d');
                    const categoryChart = new Chart(categoryCtx, {
                        type: 'pie',
                        data: {
                            labels: [
                                <c:forEach items="${categoryStats}" var="entry" varStatus="status">
                                    '${entry.key.name}'${!status.last ? ',' : ''}
                                </c:forEach>
                            ],
                            datasets: [{
                                data: [
                                    <c:forEach items="${categoryStats}" var="entry" varStatus="status">
                                        ${entry.value}${!status.last ? ',' : ''}
                                    </c:forEach>
                                ],
                                backgroundColor: [
                                    'rgba(255, 99, 132, 0.5)',
                                    'rgba(54, 162, 235, 0.5)',
                                    'rgba(255, 206, 86, 0.5)',
                                    'rgba(75, 192, 192, 0.5)',
                                    'rgba(153, 102, 255, 0.5)',
                                    'rgba(255, 159, 64, 0.5)'
                                ]
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                }
                            }
                        }
                    });
                </script>
            </c:if>
            
            <!-- Thống kê theo trạng thái -->
            <c:if test="${statisticsType == 'by_status'}">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Số lượng câu hỏi theo trạng thái</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="statusDetailChart"></canvas>
                    </div>
                </div>
                
                <div class="table-responsive mt-4">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Trạng thái</th>
                                <th>Số câu hỏi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Đã trả lời</td>
                                <td>${statusStats['answered']}</td>
                            </tr>
                            <tr>
                                <td>Đang chờ</td>
                                <td>${statusStats['pending']}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                
                <script>
                    // Biểu đồ theo trạng thái
                    const statusDetailCtx = document.getElementById('statusDetailChart').getContext('2d');
                    const statusDetailChart = new Chart(statusDetailCtx, {
                        type: 'doughnut',
                        data: {
                            labels: ['Đã trả lời', 'Đang chờ'],
                            datasets: [{
                                data: [${statusStats['answered']}, ${statusStats['pending']}],
                                backgroundColor: [
                                    'rgba(40, 167, 69, 0.5)',
                                    'rgba(255, 193, 7, 0.5)'
                                ],
                                borderColor: [
                                    'rgba(40, 167, 69, 1)',
                                    'rgba(255, 193, 7, 1)'
                                ],
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                }
                            }
                        }
                    });
                </script>
            </c:if>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
</body>
</html> 