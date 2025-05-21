# PowerShell script để cập nhật từ javax.* sang jakarta.* cho dự án Java Jakarta EE 11

# Tìm tất cả các file Java trong thư mục nguồn
$files = Get-ChildItem -Path .\src -Filter *.java -Recurse

foreach ($file in $files) {
    Write-Host "Đang xử lý file: $($file.FullName)"
    
    # Đọc nội dung file
    $content = Get-Content -Path $file.FullName -Raw
    
    # Thay thế javax.servlet bằng jakarta.servlet
    $content = $content -replace 'import javax\.servlet', 'import jakarta.servlet'
    
    # Thay thế javax.persistence bằng jakarta.persistence
    $content = $content -replace 'import javax\.persistence', 'import jakarta.persistence'
    
    # Thay thế các import javax.* khác nếu cần
    $content = $content -replace 'import javax\.websocket', 'import jakarta.websocket'
    $content = $content -replace 'import javax\.annotation', 'import jakarta.annotation'
    $content = $content -replace 'import javax\.validation', 'import jakarta.validation'
    $content = $content -replace 'import javax\.inject', 'import jakarta.inject'
    $content = $content -replace 'import javax\.enterprise', 'import jakarta.enterprise'
    $content = $content -replace 'import javax\.transaction', 'import jakarta.transaction'
    $content = $content -replace 'import javax\.xml', 'import jakarta.xml'
    $content = $content -replace 'import javax\.mail', 'import jakarta.mail'
    $content = $content -replace 'import javax\.ejb', 'import jakarta.ejb'
    $content = $content -replace 'import javax\.json', 'import jakarta.json'
    $content = $content -replace 'import javax\.ws', 'import jakarta.ws'
    $content = $content -replace 'import javax\.jms', 'import jakarta.jms'
    $content = $content -replace 'import javax\.naming', 'import jakarta.naming'
    $content = $content -replace 'import javax\.el', 'import jakarta.el'
    
    # Ghi lại nội dung đã thay thế
    Set-Content -Path $file.FullName -Value $content
    
    Write-Host "Hoàn tất: $($file.FullName)"
}

Write-Host "Quá trình chuyển đổi đã hoàn tất!" 