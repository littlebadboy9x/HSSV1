<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer class="bg-dark text-white mt-5 py-3">
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <h5>Hệ thống Tư vấn Sinh viên</h5>
                <p class="mb-0">Cung cấp thông tin, giải đáp thắc mắc cho sinh viên, phụ huynh và học sinh.</p>
            </div>
            <div class="col-md-3">
                <h5>Liên kết</h5>
                <ul class="list-unstyled">
                    <li><a href="${pageContext.request.contextPath}/home" class="text-white">Trang chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/questions" class="text-white">Câu hỏi</a></li>
                    <li><a href="${pageContext.request.contextPath}/search" class="text-white">Tìm kiếm</a></li>
                </ul>
            </div>
            <div class="col-md-3">
                <h5>Liên hệ</h5>
                <ul class="list-unstyled">
                    <li>Email: contact@hssv.com</li>
                    <li>Điện thoại: 0909 123 456</li>
                </ul>
            </div>
        </div>
        <div class="row mt-3">
            <div class="col-12 text-center">
                <p class="mb-0">&copy; <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %> Hệ thống Tư vấn Sinh viên - Bản quyền thuộc về HSSV.</p>
            </div>
        </div>
    </div>
</footer> 
