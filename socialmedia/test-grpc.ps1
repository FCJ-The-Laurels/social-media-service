# Quick gRPC Diagnostic Script
# Run this script to diagnose gRPC connectivity issues

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "gRPC Connectivity Diagnostic Tool" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Check if gRPC service port is listening
Write-Host "1. Checking if gRPC service is running on port 9090..." -ForegroundColor Yellow
$portCheck = netstat -ano | findstr ":9090"
if ($portCheck) {
    Write-Host "   ✅ Port 9090 is LISTENING" -ForegroundColor Green
    Write-Host "   $portCheck" -ForegroundColor Gray
} else {
    Write-Host "   ❌ Port 9090 is NOT listening" -ForegroundColor Red
    Write-Host "   ⚠️  The user-info gRPC service is NOT running!" -ForegroundColor Red
    Write-Host "   💡 Start your user-info service first before proceeding." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}
Write-Host ""

# Step 2: Check if Spring Boot app is running on port 8080
Write-Host "2. Checking if Spring Boot application is running on port 8080..." -ForegroundColor Yellow
$appCheck = netstat -ano | findstr ":8080"
if ($appCheck) {
    Write-Host "   ✅ Port 8080 is LISTENING" -ForegroundColor Green
    Write-Host "   $appCheck" -ForegroundColor Gray
} else {
    Write-Host "   ❌ Port 8080 is NOT listening" -ForegroundColor Red
    Write-Host "   ⚠️  Spring Boot application is NOT running!" -ForegroundColor Red
    Write-Host "   💡 Start your Spring Boot application first." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}
Write-Host ""

# Step 3: Test network connectivity to gRPC port
Write-Host "3. Testing TCP connection to gRPC service..." -ForegroundColor Yellow
try {
    $connection = Test-NetConnection -ComputerName localhost -Port 9090 -WarningAction SilentlyContinue
    if ($connection.TcpTestSucceeded) {
        Write-Host "   ✅ TCP connection to localhost:9090 SUCCESSFUL" -ForegroundColor Green
    } else {
        Write-Host "   ❌ TCP connection to localhost:9090 FAILED" -ForegroundColor Red
        Write-Host "   💡 Check firewall settings or gRPC service status." -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ⚠️  Could not test connection: $_" -ForegroundColor Yellow
}
Write-Host ""

# Step 4: Test gRPC health endpoint
Write-Host "4. Testing gRPC health check endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/health/grpc" -Method GET -ContentType "application/json" -TimeoutSec 10
    $healthData = $response.Content | ConvertFrom-Json

    if ($healthData.status -eq "UP") {
        Write-Host "   ✅ gRPC health check: SUCCESS" -ForegroundColor Green
        Write-Host "   ✅ Service: $($healthData.service)" -ForegroundColor Green
        Write-Host "   ✅ Timestamp: $($healthData.timestamp)" -ForegroundColor Green
    } else {
        Write-Host "   ❌ gRPC health check: FAILED" -ForegroundColor Red
        Write-Host "   Status: $($healthData.status)" -ForegroundColor Red
        if ($healthData.error) {
            Write-Host "   Error: $($healthData.error)" -ForegroundColor Red
        }
        Write-Host "   💡 Check if user-info service is properly configured and running." -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ❌ Could not reach health endpoint: $_" -ForegroundColor Red
    Write-Host "   💡 Make sure your Spring Boot application is running and accessible." -ForegroundColor Yellow
}
Write-Host ""

# Step 5: Test fetching a blog
Write-Host "5. Testing blog fetch (should trigger gRPC call)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/blogs/newest/paginated?page=0&size=1" -Method GET -ContentType "application/json" -TimeoutSec 10
    $blogData = $response.Content | ConvertFrom-Json

    if ($blogData.content -and $blogData.content.Count -gt 0) {
        $firstBlog = $blogData.content[0]
        Write-Host "   ✅ Blog fetch: SUCCESS" -ForegroundColor Green
        Write-Host "   Title: $($firstBlog.title)" -ForegroundColor Cyan

        if ($firstBlog.authorName -and $firstBlog.authorName -ne "Unknown User") {
            Write-Host "   ✅ Author Name: $($firstBlog.authorName)" -ForegroundColor Green
            Write-Host "   ✅ gRPC IS WORKING! Author info was fetched successfully." -ForegroundColor Green
        } else {
            Write-Host "   ❌ Author Name: $($firstBlog.authorName)" -ForegroundColor Red
            Write-Host "   ⚠️  gRPC is NOT fetching user info properly!" -ForegroundColor Red
            Write-Host "   💡 Check application logs for gRPC errors." -ForegroundColor Yellow
        }

        if ($firstBlog.authorAvatar) {
            Write-Host "   ✅ Author Avatar: $($firstBlog.authorAvatar)" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  Author Avatar: (empty)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   ⚠️  No blogs found in database" -ForegroundColor Yellow
        Write-Host "   💡 Create a blog first to test gRPC integration." -ForegroundColor Yellow
    }
} catch {
    Write-Host "   ❌ Could not fetch blogs: $_" -ForegroundColor Red
    Write-Host "   💡 Check application logs for errors." -ForegroundColor Yellow
}
Write-Host ""

# Step 6: System information
Write-Host "6. Getting system information..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/health/system" -Method GET -ContentType "application/json" -TimeoutSec 10
    $sysData = $response.Content | ConvertFrom-Json
    Write-Host "   Java Version: $($sysData.javaVersion)" -ForegroundColor Cyan
    Write-Host "   OS: $($sysData.osName) $($sysData.osVersion)" -ForegroundColor Cyan
    Write-Host "   Free Memory: $($sysData.freeMemory)" -ForegroundColor Cyan
} catch {
    Write-Host "   ⚠️  Could not get system info" -ForegroundColor Yellow
}
Write-Host ""

# Summary
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Diagnostic Summary" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. If gRPC health check failed:" -ForegroundColor White
Write-Host "   - Verify user-info service is running" -ForegroundColor Gray
Write-Host "   - Check application.properties for correct gRPC address" -ForegroundColor Gray
Write-Host "   - Review startup logs in console" -ForegroundColor Gray
Write-Host ""
Write-Host "2. If author name is 'Unknown User':" -ForegroundColor White
Write-Host "   - Check application logs for gRPC errors" -ForegroundColor Gray
Write-Host "   - Verify user exists in user-info database" -ForegroundColor Gray
Write-Host "   - Test with: curl http://localhost:8080/api/health/grpc/test/{userId}" -ForegroundColor Gray
Write-Host ""
Write-Host "3. For detailed troubleshooting:" -ForegroundColor White
Write-Host "   - Read: GRPC_TROUBLESHOOTING.md" -ForegroundColor Gray
Write-Host "   - Enable DEBUG logging in application.properties" -ForegroundColor Gray
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan

