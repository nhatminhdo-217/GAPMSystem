// notification.js
var stompClient = null;

function connectWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);

        // Lấy ID của user hiện tại
        var userId = $('#current-user-id').val();
        console.log("Current user ID for WebSocket subscription:", userId);  // Thêm log

        if (!userId) {
            console.error("User ID not found in the page");
            return;
        }

        // Subscribe đến channel cá nhân của user
        stompClient.subscribe('/user/' + userId + '/queue/notifications', function(notification) {
            console.log("Received WebSocket notification:", notification);  // Thêm log

            try {
                var notificationData = JSON.parse(notification.body);
                displayNotification(notificationData);
                updateNotificationCount();

                // Refresh dropdown nếu đang mở
                if ($('#notificationDropdown').hasClass('show')) {
                    loadNotificationDropdown();
                }
            } catch (e) {
                console.error("Error processing notification:", e);
            }
        });
    }, function(error) {
        console.log('Error connecting to WebSocket: ' + error);
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
    // Validate notification ID
    if (!notificationId) {
        console.error("Error: Missing notification ID");
        return;
    }

    console.log("Marking notification as read: " + notificationId);

    // Get CSRF token
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // Validate CSRF token
    if (!token || !header) {
        console.error("Error: CSRF token or header not found");
        console.log("Token:", token);
        console.log("Header:", header);
        return;
    }

    // Create a form data object
    var formData = new FormData();
    formData.append("_csrf", token);

    $.ajax({
        type: "POST",
        url: "/notifications/api/read/" + notificationId,
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response) {
            // Handle success
            updateNotificationCount();
            console.log('Successfully marked notification ' + notificationId + ' as read');

            // Update UI
            $('.notification-item[data-id="' + notificationId + '"]').removeClass('unread');
            $('.notification-dropdown-item[data-id="' + notificationId + '"]').removeClass('unread');

            // If on notifications page, refresh the list
            if (window.location.pathname.includes('/notifications')) {
                loadNotifications();
            }
        },
        error: function(xhr, status, error) {
            console.error("Error marking notification as read:");
            console.error("Status:", status);
            console.error("Error:", error);
            console.error("Response:", xhr.responseText);

            // Try an alternative approach for CSRF if the current one fails
            if (xhr.status === 403) {
                console.log("Trying alternative CSRF approach...");
                var csrfToken = $('input[name="_csrf"]').val();
                if (csrfToken) {
                    $.ajax({
                        type: "POST",
                        url: "/notifications/api/read/" + notificationId,
                        headers: {
                            'X-CSRF-TOKEN': csrfToken
                        },
                        success: function() {
                            updateNotificationCount();
                            console.log('Successfully marked notification ' + notificationId + ' as read (alternative method)');
                            if (window.location.pathname.includes('/notifications')) {
                                loadNotifications();
                            }
                        },
                        error: function(xhr2, status2, error2) {
                            console.error("Alternative method also failed:", error2);
                        }
                    });
                }
            }
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
                loadNotificationDropdown()
            }
        }
    });
}

// function loadNotifications(page = 0) {
//     $.get("/notifications/api/list?page=" + page, function(data) {
//         // Xóa danh sách thông báo hiện tại
//         $('#notifications-list').empty();
//
//         // Thêm các thông báo mới
//         data.notifications.forEach(function(notification) {
//             var item =
//                 '<div class="notification-item ' + (notification.read ? '' : 'unread') + '" data-id="' + notification.id + '">' +
//                 '<div class="notification-header">' +
//                 '<span class="notification-source">' + notification.source + '</span>' +
//                 '<span class="notification-time">' + formatTime(notification.createAt) + '</span>' +
//                 '</div>' +
//                 '<div class="notification-message">' + notification.message + '</div>' +
//                 '<div class="notification-actions">' +
//                 (notification.targetUrl ? '<a href="' + notification.targetUrl + '" class="btn btn-sm btn-primary">View</a>' : '') +
//                 (notification.read ? '' : '<button class="btn btn-sm btn-outline-secondary mark-as-read-btn">Mark as read</button>') +
//                 '</div>' +
//                 '</div>';
//
//             $('#notification-dropdown-items').append(item);
//             $('#notifications-list').append(item);
//         });
//
//         // Cập nhật phân trang
//         updatePagination(data.currentPage, data.totalPages);
//
//         // Cập nhật số thông báo chưa đọc
//         if (data.unreadCount > 0) {
//             $('#notification-badge').text(data.unreadCount).show();
//         } else {
//             $('#notification-badge').hide();
//         }
//     });
// }

// Trong notification.js - Hàm tải dropdown notifications
function loadNotificationDropdown() {
    $.get("/notifications/api/dropdown", function(data) {
        console.log("Received notifications data:", data);  // Thêm log này để gỡ lỗi

        $('#notification-dropdown-items').empty();

        if (!data.notifications || data.notifications.length === 0) {
            $('#notification-dropdown-items').append('<div class="dropdown-item text-center">Không có thông báo mới</div>');
            return;
        }

        // Thêm các thông báo mới ${formatTime(notification.timestamp)}
        data.notifications.forEach(function(notification) {
            console.log("Processing notification:", notification);  // Thêm log cho từng thông báo
            var item =
                `
                <div class="dropdown-item-text notification-dropdown-item ${notification.read ? '' : 'unread'}" data-id="${notification.id}">
                    <div class="d-flex justify-content-between">
                        <small class="fw-bold"> ${notification.source || 'Hệ thống'} </small>
                        <small class="text-muted"> ${formatTimeShort(notification.timestamp)} </small>
                    </div>
                    <p class="mb-1 notification-text"> ${notification.message || 'Không có nội dung'} </p>
                    <div class="d-flex ${notification.targetUrl ? 'justify-content-between' : 'justify-content-end'} mt-1">
                        ${notification.targetUrl ? '<a href="' + notification.targetUrl + '" class="btn btn-sm btn-primary px-2 py-0 notification-link" data-id="' + notification.id + '">Xem</a>' : ''}
                    </div>
                </div>
                <li><hr class="dropdown-divider my-1"></li>             
            `

            $('#notification-dropdown-items').append(item);

            // Show empty state if no notifications
            if (data.notifications.length === 0) {
                $('#notification-dropdown-items').html('<div class="text-center p-3 text-muted"><small>Không có thông báo nào</small></div>');
            }
        });

        // Cập nhật số thông báo chưa đọc
        if (data.unreadCount > 0) {
            $('#notification-badge').text(data.unreadCount).show();
        } else {
            $('#notification-badge').hide();
        }

    }).fail(function(xhr, status, error) {
        console.error("Error loading notifications:", error);
        console.error("Response:", xhr.responseText);  // Thêm chi tiết lỗi
        $('#notification-dropdown-items').empty().append('<div class="dropdown-item text-center">Lỗi tải thông báo</div>');
    });
}

function formatTimeShort(timestamp) {
    var date = new Date(timestamp);
    var now = new Date();
    var diffMs = now - date;
    var diffMins = Math.round(diffMs / 60000);
    var diffHours = Math.round(diffMs / 3600000);
    var diffDays = Math.round(diffMs / 86400000);

    if (diffMins < 1) {
        return 'Vừa xong';
    } else if (diffMins < 60) {
        return diffMins + ' phút trước';
    } else if (diffHours < 24) {
        return diffHours + ' giờ trước';
    } else if (diffDays < 7) {
        return diffDays + ' ngày trước';
    } else {
        return date.toLocaleDateString();
    }
}

function formatDateAndTime(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}/${month} ${hours}:${minutes}`;
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
    // loadNotifications();
    loadNotificationDropdown();

    // Cập nhật số thông báo chưa đọc
    updateNotificationCount();

    // Xử lý sự kiện click vào nút đánh dấu đã đọc
    $(document).on('click', '.mark-as-read-btn', function(e) {
        e.preventDefault();
        console.log('Marking notification as read');
        var notificationId = $(this).closest('.notification-dropdown-item').data('id');
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
        loadNotificationDropdown();
    });

    // Nếu đang ở trang thông báo, tải danh sách thông báo
    if (window.location.pathname.includes('/notifications')) {
        loadNotifications();
        loadNotificationDropdown();
    };

    $(document).on('click', '.notification-link', function(e) {
        // Don't prevent default here to allow the link to work normally
        var notificationId = $(this).data('id');
        if (notificationId) {
            markAsRead(notificationId);
        }
    });
});