# S3 Image Upload API Documentation

## Overview
This document describes the S3 image upload functionality added to the Image Controller. Users can now upload images directly to AWS S3 and receive a public URL for the uploaded image.

## Files Created/Modified

### 1. New Files Created

#### `AwsS3Config.java`
- **Location**: `src/main/java/FCJLaurels/awsrek/config/AwsS3Config.java`
- **Purpose**: Configures AWS S3 client with credentials and region
- **Key Components**:
  - Reads AWS credentials from application.properties
  - Creates and configures S3Client bean
  - Uses StaticCredentialsProvider for authentication

#### `S3Service.java`
- **Location**: `src/main/java/FCJLaurels/awsrek/service/aws/S3Service.java`
- **Purpose**: Handles all S3 operations (upload, delete, file existence check)
- **Key Methods**:
  - `uploadFile(MultipartFile)`: Uploads file to S3 with unique UUID-based filename
  - `deleteFile(String)`: Deletes file from S3 bucket
  - `fileExists(String)`: Checks if file exists in S3
- **Features**:
  - Automatic unique filename generation using UUID
  - Public read ACL for uploaded files
  - Content type detection
  - Comprehensive error logging

### 2. Modified Files

#### `application.properties`
Added AWS S3 configuration:
```properties
# AWS S3 Configuration
aws.s3.bucket-name=your-bucket-name
aws.s3.region=ap-southeast-1
aws.access-key-id=${AWS_ACCESS_KEY_ID:your-access-key}
aws.secret-access-key=${AWS_SECRET_ACCESS_KEY:your-secret-key}
```

#### `ImageService.java`
Added new method signature:
```java
ImageDTO uploadImageToS3(MultipartFile file) throws IOException;
```

#### `ImageServiceImplementation.java`
Implemented the upload logic:
- Validates file is not empty
- Extracts file metadata (name, type)
- Uploads to S3 via S3Service
- Saves image metadata to MongoDB
- Returns ImageDTO with S3 URL

#### `ImageController.java`
Added new endpoint `/images/upload`:
- Accepts multipart/form-data
- Validates file size (10MB limit)
- Validates file type (images only)
- Returns ImageDTO with S3 URL on success

## API Endpoint

### Upload Image to S3

**Endpoint**: `POST /images/upload`

**Content-Type**: `multipart/form-data`

**Request Parameter**:
- `file` (required): The image file to upload

**Validations**:
1. File must not be empty
2. File size must not exceed 10MB
3. File must be an image (content-type starts with "image/")

**Response Codes**:
- `201 CREATED`: Image successfully uploaded
- `400 BAD REQUEST`: Invalid file, empty file, or validation error
- `500 INTERNAL SERVER ERROR`: Server error or S3 upload failure

**Success Response Example**:
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "profile-pic.jpg",
  "url": "https://your-bucket-name.s3.ap-southeast-1.amazonaws.com/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
  "type": "jpg",
  "creationDate": "2025-11-07T10:30:00"
}
```

**Error Response Examples**:
```json
// Empty file
"File is empty"

// File too large
"File size exceeds maximum limit of 10MB"

// Invalid file type
"Only image files are allowed"

// Upload failure
"Failed to upload image: <error details>"
```

## Configuration Steps

### 1. Update Application Properties
Replace the placeholder values in `application.properties`:
```properties
aws.s3.bucket-name=your-actual-bucket-name
aws.s3.region=ap-southeast-1  # or your region
```

### 2. Set AWS Credentials
You can set credentials in two ways:

**Option A: Environment Variables** (Recommended for production)
```bash
export AWS_ACCESS_KEY_ID=your_actual_access_key
export AWS_SECRET_ACCESS_KEY=your_actual_secret_key
```

**Option B: Direct in Properties** (For development only)
```properties
aws.access-key-id=your_actual_access_key
aws.secret-access-key=your_actual_secret_key
```

### 3. Configure S3 Bucket
Ensure your S3 bucket has proper permissions:
1. Bucket policy allows PutObject operations
2. Public access settings allow public read (if you want public URLs)
3. CORS configuration (if accessing from browser):
```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": []
    }
]
```

## Testing with cURL

### Upload Image
```bash
curl -X POST http://localhost:8080/images/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/your/image.jpg"
```

### Upload with JWT Authentication (if required)
```bash
curl -X POST http://localhost:8080/images/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/your/image.jpg"
```

## Testing with Swagger UI

1. Navigate to: `http://localhost:8080/swagger-ui.html`
2. Find "Image Management" section
3. Locate `POST /images/upload` endpoint
4. Click "Try it out"
5. Click "Choose File" and select an image
6. Click "Execute"
7. View the response with the S3 URL

## Security Considerations

1. **Bucket Security**: 
   - Set appropriate bucket policies
   - Consider using private buckets with pre-signed URLs for sensitive content
   - Enable bucket versioning for recovery

2. **File Validation**:
   - Already validates file type (images only)
   - Already validates file size (10MB limit)
   - Consider adding virus scanning for production

3. **Credentials**:
   - Never commit credentials to source control
   - Use environment variables or AWS IAM roles
   - Rotate credentials regularly

4. **Rate Limiting**:
   - Consider adding rate limiting to prevent abuse
   - Monitor S3 usage and costs

## Features

✅ Automatic unique filename generation (UUID-based)
✅ Content-type detection and validation
✅ File size validation (10MB limit)
✅ Image type validation
✅ Public URL generation
✅ Metadata saved to MongoDB
✅ Comprehensive error handling
✅ Swagger/OpenAPI documentation
✅ Logging for debugging

## Architecture Flow

1. **User uploads file** → ImageController
2. **Controller validates** → File size, type, not empty
3. **Service processes** → ImageServiceImplementation
4. **Upload to S3** → S3Service.uploadFile()
5. **Generate URL** → Format: `https://{bucket}.s3.{region}.amazonaws.com/{filename}`
6. **Save metadata** → MongoDB (ImageRepository)
7. **Return response** → ImageDTO with S3 URL

## Future Enhancements

- [ ] Image resizing/optimization before upload
- [ ] Support for private images with pre-signed URLs
- [ ] Batch upload support
- [ ] Image compression
- [ ] AWS Rekognition integration for content moderation
- [ ] Thumbnail generation
- [ ] CDN integration (CloudFront)
- [ ] Delete from S3 when deleting image record from MongoDB

## Troubleshooting

### Issue: "Access Denied" when uploading
**Solution**: Check your AWS credentials and bucket permissions

### Issue: "Bucket not found"
**Solution**: Verify bucket name and region in application.properties

### Issue: "File size limit exceeded"
**Solution**: Either reduce file size or increase limit in ImageController

### Issue: Images not accessible via URL
**Solution**: Check bucket public access settings and ACL configuration

## Dependencies

The project already includes the required AWS SDK dependency:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.26</version>
</dependency>
```

No additional dependencies needed.

## Notes

- Files are uploaded with public-read ACL by default
- Filenames are automatically generated as UUIDs to prevent collisions
- Original filename and metadata are preserved in MongoDB
- File extension is preserved from original filename
- All operations are logged for debugging

---

**Created**: November 7, 2025
**Version**: 1.0.0

