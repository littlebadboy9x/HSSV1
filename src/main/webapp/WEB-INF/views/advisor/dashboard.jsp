<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Cố Vấn - Hệ Thống Tư Vấn Sinh Viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="../includes/header.jsp" />
    
    <div class="container py-4">
        <div class="row mb-4">
            <div class="col">
                <h2>Dashboard Cố Vấn</h2>
                <p class="text-muted">Xin chào, ${sessionScope.user.fullName}</p>
            </div>
        </div>
        
        <!-- Thống kê tổng quan -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Câu hỏi chưa trả lời</h5>
                        <p class="card-text display-4">${unansweredQuestions.size()}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Câu hỏi đã trả lời</h5>
                        <p class="card-text display-4">${answeredQuestions.size()}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Tổng số câu trả lời</h5>
                        <p class="card-text display-4">${totalAnswers}</p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Danh sách câu hỏi chưa trả lời -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title mb-0">Câu hỏi chưa trả lời trong khoa của bạn</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty unansweredQuestions}">
                    <p class="text-muted">Không có câu hỏi nào chưa được trả lời.</p>
                </c:if>
                <c:if test="${not empty unansweredQuestions}">
                    <div class="list-group">
                        <c:forEach var="question" items="${unansweredQuestions}">
                            <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" 
                               class="list-group-item list-group-item-action">
                                <div class="d-flex w-100 justify-content-between">
                                    <h6 class="mb-1">${question.title}</h6>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </small>
                                </div>
                                <p class="mb-1 text-truncate">${question.content}</p>
                                <small class="text-muted">
                                    Bởi: ${question.user.fullName}
                                </small>
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
        
        <!-- Danh sách câu hỏi đã trả lời -->
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Câu hỏi bạn đã trả lời</h5>
            </div>
            <div class="card-body">
                <c:if test="${empty answeredQuestions}">
                    <p class="text-muted">Bạn chưa trả lời câu hỏi nào.</p>
                </c:if>
                <c:if test="${not empty answeredQuestions}">
                    <div class="list-group">
                        <c:forEach var="question" items="${answeredQuestions}">
                            <a href="${pageContext.request.contextPath}/questions/detail?id=${question.id}" 
                               class="list-group-item list-group-item-action">
                                <div class="d-flex w-100 justify-content-between">
                                    <h6 class="mb-1">${question.title}</h6>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${question.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </small>
                                </div>
                                <p class="mb-1 text-truncate">${question.content}</p>
                                <small class="text-muted">
                                    Bởi: ${question.user.fullName}
                                </small>
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 
