# A-mobile-app-for-disabled-people

Ứng dụng hỗ trợ người khiếm thị di chuyển sử dụng mô hình object detection và mô hình visual-language PaliGemma. Cách cài đặt:
- Clone repo về và cài đặt Android Studio
- Build gradle và cài đặt máy ảo/kết nối với điện thoại Android
- Chạy project

Chức năng chính sử dụng GenAI (do em cài đặt): sinh ra một câu mô tả ngắn gọn về môi trường xung quanh người dùng và cho phép họ hỏi đáp về môi trường xung quanh trông ra sao. Người dùng sẽ tiến hành chụp ảnh và app sẽ sử dụng mô hình PaliGemma, một mô hình vision-language của Google, để thực hiện image captioning (mô tả bức ảnh một cách ngắn gọn) và hỗ trợ hỏi-đáp dựa trên nội dung hình ảnh đầu vào thông qua API của Hugging Face.
