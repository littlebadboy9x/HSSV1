#!/bin/bash

# Script để cập nhật từ javax.* sang jakarta.* cho dự án Java Jakarta EE 11

# Tìm tất cả các file Java trong thư mục nguồn
find ./src -name "*.java" -type f -print0 | while IFS= read -r -d '' file; do
    echo "Đang xử lý file: $file"
    
    # Thay thế javax.servlet bằng jakarta.servlet
    sed -i 's/import javax\.servlet/import jakarta.servlet/g' "$file"
    
    # Thay thế javax.persistence bằng jakarta.persistence
    sed -i 's/import javax\.persistence/import jakarta.persistence/g' "$file"
    
    # Thay thế các import javax.* khác nếu cần
    sed -i 's/import javax\.websocket/import jakarta.websocket/g' "$file"
    sed -i 's/import javax\.annotation/import jakarta.annotation/g' "$file"
    sed -i 's/import javax\.validation/import jakarta.validation/g' "$file"
    sed -i 's/import javax\.inject/import jakarta.inject/g' "$file"
    sed -i 's/import javax\.enterprise/import jakarta.enterprise/g' "$file"
    sed -i 's/import javax\.transaction/import jakarta.transaction/g' "$file"
    sed -i 's/import javax\.xml/import jakarta.xml/g' "$file"
    sed -i 's/import javax\.mail/import jakarta.mail/g' "$file"
    sed -i 's/import javax\.ejb/import jakarta.ejb/g' "$file"
    sed -i 's/import javax\.json/import jakarta.json/g' "$file"
    sed -i 's/import javax\.ws/import jakarta.ws/g' "$file"
    sed -i 's/import javax\.jms/import jakarta.jms/g' "$file"
    sed -i 's/import javax\.naming/import jakarta.naming/g' "$file"
    
    echo "Hoàn tất: $file"
done

echo "Quá trình chuyển đổi đã hoàn tất!" 