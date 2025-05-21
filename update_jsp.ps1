# PowerShell script để cập nhật taglib trong JSP từ java.sun.com sang jakarta

# Tìm tất cả các file JSP trong thư mục webapp
$files = Get-ChildItem -Path .\src\main\webapp -Filter *.jsp -Recurse

foreach ($file in $files) {
    Write-Host "Đang xử lý file: $($file.FullName)"
    
    # Đọc nội dung file
    $content = Get-Content -Path $file.FullName -Raw
    
    # Thay thế các taglib
    $content = $content -replace 'uri="http://java.sun.com/jsp/jstl/core"', 'uri="jakarta.tags.core"'
    $content = $content -replace 'uri="http://java.sun.com/jsp/jstl/fmt"', 'uri="jakarta.tags.fmt"'
    $content = $content -replace 'uri="http://java.sun.com/jsp/jstl/functions"', 'uri="jakarta.tags.functions"'
    $content = $content -replace 'uri="http://java.sun.com/jsp/jstl/sql"', 'uri="jakarta.tags.sql"'
    $content = $content -replace 'uri="http://java.sun.com/jsp/jstl/xml"', 'uri="jakarta.tags.xml"'
    
    # Ghi lại nội dung đã thay thế
    Set-Content -Path $file.FullName -Value $content
    
    Write-Host "Hoàn tất: $($file.FullName)"
}

Write-Host "Quá trình cập nhật JSP đã hoàn tất!" 