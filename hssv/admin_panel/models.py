from django.db import models
from accounts.models import CustomUser
from questions.models import Department

# Create your models here.

class AdvisorProfile(models.Model):
    ROLE_CHOICES = (
        ('admin', 'Admin hệ thống'),
        ('advisor', 'Ban tư vấn'),
    )
    
    user = models.OneToOneField(CustomUser, on_delete=models.CASCADE, verbose_name="Người dùng")
    department = models.ForeignKey(Department, on_delete=models.SET_NULL, null=True, blank=True, verbose_name="Khoa/Đơn vị")
    role = models.CharField(max_length=20, choices=ROLE_CHOICES, verbose_name="Vai trò")
    
    def __str__(self):
        return f"{self.user.username} - {self.get_role_display()}"
    
    class Meta:
        verbose_name = "Hồ sơ tư vấn viên"
        verbose_name_plural = "Hồ sơ tư vấn viên"
