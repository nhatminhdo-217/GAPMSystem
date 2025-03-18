document.addEventListener('DOMContentLoaded', function() {
    // Các phần tử DOM
    const video = document.getElementById('video');
    const canvas = document.getElementById('canvas');
    const captureBtn = document.getElementById('captureBtn');
    const switchCameraBtn = document.getElementById('switchCamera');
    const preview = document.getElementById('preview');
    const previewContainer = document.querySelector('.preview-container');
    const cameraContainer = document.querySelector('.camera-container');
    const retakeBtn = document.getElementById('retakeBtn');
    const saveBtn = document.getElementById('saveBtn');
    const imagesList = document.getElementById('imagesList');

    // Các biến
    let stream = null;
    let facingMode = 'environment'; // 'environment' (camera sau) hoặc 'user' (camera trước)
    let capturedImage = null;
    let latestSavedImage = null; // Biến để lưu ảnh mới nhất

    // Khởi động camera
    async function startCamera() {
        try {
            if (stream) {
                // Dừng tất cả các track nếu stream đã tồn tại
                stream.getTracks().forEach(track => track.stop());
            }

            // Yêu cầu quyền truy cập camera với facingMode được chỉ định
            stream = await navigator.mediaDevices.getUserMedia({
                video: {
                    facingMode: facingMode,
                    width: { ideal: 1280 },
                    height: { ideal: 720 }
                },
                audio: false
            });

            // Kết nối stream với video element
            video.srcObject = stream;

            // Hiển thị container camera và ẩn preview
            cameraContainer.style.display = 'block';
            previewContainer.style.display = 'none';

        } catch (error) {
            console.error('Lỗi khi truy cập camera:', error);
            alert('Không thể truy cập camera. Vui lòng kiểm tra quyền truy cập camera.');
        }
    }

    // Chức năng chụp ảnh
    function captureImage() {
        // Thiết lập kích thước canvas bằng với kích thước video hiện tại
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;

        // Vẽ frame hiện tại của video lên canvas
        const context = canvas.getContext('2d');
        context.drawImage(video, 0, 0, canvas.width, canvas.height);

        // Chuyển đổi nội dung canvas thành URL dữ liệu
        capturedImage = canvas.toDataURL('image/png');

        // Hiển thị ảnh đã chụp
        preview.src = capturedImage;

        // Hiển thị container preview và ẩn camera
        cameraContainer.style.display = 'none';
        previewContainer.style.display = 'block';
    }

    // Đổi camera trước/sau
    function toggleCamera() {
        // Đổi giá trị facingMode
        facingMode = facingMode === 'environment' ? 'user' : 'environment';

        // Khởi động lại camera với facingMode mới
        startCamera();
    }

    // Lưu ảnh đã chụp
    async function saveImage() {
        if (!capturedImage) {
            alert('Không có ảnh để lưu. Vui lòng chụp ảnh trước.');
            return;
        }

        // ID của purchase order (có thể lấy từ tham số hoặc biến khác)
        const purchaseOrderId = getPurchaseOrderId();

        try {
            // Gửi ảnh lên server với đúng đường dẫn API
            const response = await fetch(`/purchase-order/detail/${purchaseOrderId}/contract/upload/camera`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    image: capturedImage
                })
            });

            const result = await response.json();

            if (result.success === 'true') {
                // Lưu ảnh mới nhất
                latestSavedImage = capturedImage;

                // Xóa tất cả ảnh hiện tại trong danh sách
                imagesList.innerHTML = '';

                // Chỉ hiển thị ảnh mới nhất
                const imageItem = document.createElement('div');
                imageItem.className = 'image-item latest-image';

                const img = document.createElement('img');
                img.src = latestSavedImage;
                img.alt = result.fileName;

                imageItem.appendChild(img);
                imagesList.appendChild(imageItem);

                // Quay lại chế độ camera
                startCamera();

                // Hiển thông báo thành công
                alert('Ảnh đã được lưu thành công!');
            } else {
                alert('Lỗi khi lưu ảnh: ' + (result.error || 'Lỗi không xác định'));
            }
        } catch (error) {
            console.error('Lỗi khi lưu ảnh:', error);
            alert('Không thể lưu ảnh. Vui lòng thử lại sau.');
        }
    }

    // Gắn các event listeners
    captureBtn.addEventListener('click', captureImage);
    switchCameraBtn.addEventListener('click', toggleCamera);
    retakeBtn.addEventListener('click', startCamera);
    saveBtn.addEventListener('click', saveImage);

    // Kiểm tra xem có ảnh đã lưu trước đó không
    const checkForSavedImage = () => {
        if (latestSavedImage) {
            // Xóa tất cả ảnh hiện tại trong danh sách
            imagesList.innerHTML = '';

            // Hiển thị ảnh đã lưu trước đó
            const imageItem = document.createElement('div');
            imageItem.className = 'image-item latest-image';

            const img = document.createElement('img');
            img.src = latestSavedImage;
            img.alt = 'Latest saved image';

            imageItem.appendChild(img);
            imagesList.appendChild(imageItem);
        }
    };

    // Hàm để lấy purchase order ID từ URL hoặc từ nơi khác
    function getPurchaseOrderId() {
        // Phương pháp 1: Lấy từ URL (ví dụ: ?orderId=123)
        const urlParams = new URLSearchParams(window.location.search);
        const idFromUrl = urlParams.get('orderId');
        if (idFromUrl) return idFromUrl;

        // Phương pháp 2: Lấy từ đường dẫn URL (ví dụ: /orders/123/edit)
        const pathMatch = window.location.pathname.match(/\/purchase-order\/detail\/(\d+)/);
        if (pathMatch && pathMatch[1]) return pathMatch[1];

        // Phương pháp 3: Lấy từ trường hidden (nếu có)
        const hiddenField = document.getElementById('purchaseOrderId');
        if (hiddenField) return hiddenField.value;

        // Giá trị mặc định nếu không tìm thấy
        return null; // Thay thế bằng ID mặc định hoặc throw error
    }

    // Kiểm tra hỗ trợ getUserMedia
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        // Khởi động camera khi trang được tải
        startCamera();
    } else {
        alert('Trình duyệt của bạn không hỗ trợ WebRTC. Vui lòng sử dụng Chrome, Firefox, Safari hoặc Edge phiên bản mới nhất.');
    }
});