// notification.js
var stompClient = null;

function connectWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);

        // Lấy ID của user hiện tại từ dữ liệu được thiết lập trong HTML
        var userId = $('#current-user-id').val();

        // Subscribe đến channel cá nhân của user
        stompClient.subscribe('/user/' + userId + '/queue/notifications', function(notification) {
            var notificationData = JSON.parse(notification.body);
            displayNotification(notificationData);
            updateNotificationCount();
        });
    }, function(error) {
        console.log('Error connecting to WebSocket: ' + error);
        // Cố gắng kết nối lại sau 5 giây
        setTimeout(connectWebSocket, 5000);
    });
}

function displayNotification(notification) {
    // Hiển thị toast notification
    var toast =
        '<div class="toast show" role="alert" aria-live="assertive" aria-atomic="true" data-notification-id="' + notification.id + '">' +
        '<div class="toast-header">' +
        '<strong class="me-auto">' + notification.source + '</strong>' +
        '<small class="text-muted">' + formatTime(notification.timestamp) + '</small>' +
        '<button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>' +
        '</div>' +
        '<div class="toast-body">' + notification.message + '</div>' +
        '</div>';

    $('#notification-container').prepend(toast);

    // Tự động xóa toast sau 5 giây
    setTimeout(function() {
        $('.toast[data-notification-id="' + notification.id + '"]').remove();
    }, 5000);
}

function updateNotificationCount() {
    $.get("/notifications/api/count", function(data) {
        var count = data.unreadCount;
        if (count > 0) {
            $('#notification-badge').text(count).show();
        } else {
            $('#notification-badge').hide();
        }
    });
}

function markAsRead(notificationId) {
    $.ajax({
        type: "POST",
        url: "/notifications/api/read/" + notificationId,
        beforeSend: function(xhr) {
            // Get CSRF token from meta tag or hidden input field
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            xhr.setRequestHeader(header, token);
        },
        success: function() {
            // Handle success
            updateNotificationCount();
            console.log('Marked notification ' + notificationId + ' as read');
            if (window.location.pathname.includes('/notifications')) {
                loadNotifications();
            }
        },
        error: function(xhr, status, error) {
            console.error("Error marking notification as read:", error);
        }
    });
}

function markAllAsRead() {
    $.ajax({
        type: "POST",
        url: "/notifications/api/read-all",
        beforeSend: function(xhr) {
            // Get CSRF token from meta tag or hidden input field
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            xhr.setRequestHeader(header, token);
        },
        success: function() {
            // Handle success
            updateNotificationCount();
            console.log('Marked all notifications as read');
            if (window.location.pathname.includes('/notifications')) {
                loadNotifications();
            }
        }
    });
}

function loadNotifications(page = 0) {
    $.get("/notifications/api/list?page=" + page, function(data) {
        // Xóa danh sách thông báo hiện tại
        $('#notifications-list').empty();

        // Thêm các thông báo mới
        data.notifications.forEach(function(notification) {
            var item =
                '<div class="notification-item ' + (notification.read ? '' : 'unread') + '" data-id="' + notification.id + '">' +
                '<div class="notification-header">' +
                '<span class="notification-source">' + notification.source + '</span>' +
                '<span class="notification-time">' + formatTime(notification.createAt) + '</span>' +
                '</div>' +
                '<div class="notification-message">' + notification.message + '</div>' +
                '<div class="notification-actions">' +
                (notification.targetUrl ? '<a href="' + notification.targetUrl + '" class="btn btn-sm btn-primary">View</a>' : '') +
                (notification.read ? '' : '<button class="btn btn-sm btn-outline-secondary mark-as-read-btn">Mark as read</button>') +
                '</div>' +
                '</div>';

            $('#notification-dropdown-items').append(item);
            $('#notifications-list').append(item);
        });

        // Cập nhật phân trang
        updatePagination(data.currentPage, data.totalPages);

        // Cập nhật số thông báo chưa đọc
        if (data.unreadCount > 0) {
            $('#notification-badge').text(data.unreadCount).show();
        } else {
            $('#notification-badge').hide();
        }
    });
}

function updatePagination(currentPage, totalPages) {
    var pagination = $('#notifications-pagination');
    pagination.empty();

    if (totalPages <= 1) {
        return;
    }

    // Tạo nút Previous
    var prevDisabled = currentPage === 0 ? 'disabled' : '';
    pagination.append('<li class="page-item ' + prevDisabled + '"><a class="page-link" href="javascript:void(0)" data-page="' + (currentPage - 1) + '">Previous</a></li>');

    // Tạo các nút số trang
    for (var i = 0; i < totalPages; i++) {
        var active = i === currentPage ? 'active' : '';
        pagination.append('<li class="page-item ' + active + '"><a class="page-link" href="javascript:void(0)" data-page="' + i + '">' + (i + 1) + '</a></li>');
    }

    // Tạo nút Next
    var nextDisabled = currentPage === totalPages - 1 ? 'disabled' : '';
    pagination.append('<li class="page-item ' + nextDisabled + '"><a class="page-link" href="javascript:void(0)" data-page="' + (currentPage + 1) + '">Next</a></li>');
}

function formatTime(timestamp) {
    var date = new Date(timestamp);
    return date.toLocaleString();
}

$(document).ready(function() {
    // Kết nối WebSocket khi trang được tải
    connectWebSocket();

    // Tải danh sách thông báo khi trang được tải
    loadNotifications();

    // Cập nhật số thông báo chưa đọc
    updateNotificationCount();

    // Xử lý sự kiện click vào nút đánh dấu đã đọc
    $(document).on('click', '.mark-as-read-btn', function(e) {
        e.preventDefault();
        console.log('Marking notification as read');
        var notificationId = $(this).closest('.notification-item').data('id');
        markAsRead(notificationId);
    });

    // Xử lý sự kiện click vào nút đánh dấu tất cả đã đọc
    $('#mark-all-as-read-btn').click(function(e) {
        e.preventDefault();
        markAllAsRead();
    });

    // Xử lý sự kiện click vào các nút phân trang
    $(document).on('click', '#notifications-pagination .page-link', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        loadNotifications(page);
    });

    // Nếu đang ở trang thông báo, tải danh sách thông báo
    if (window.location.pathname.includes('/notifications')) {
        loadNotifications();
    }
});