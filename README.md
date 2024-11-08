# A-mobile-app-for-disabled-people

Ứng dụng hỗ trợ người khiếm thị di chuyển sử dụng mô hình object detection và mô hình visual-language PaliGemma. Cách cài đặt:
- Clone repo về và cài đặt Android Studio
- Build gradle và cài đặt máy ảo/kết nối với điện thoại Android
- Chạy project

Ứng dụng vẫn đang quá trình hoàn thiện và gồm 3 chức năng chính: 
- Chức năng 1 (đang hoàn thiện): sinh ra một câu mô tả ngắn gọn về môi trường xung quanh người dùng và cho phép họ hỏi đáp về môi trường xung quanh trông ra sao. Người dùng sẽ tiến hành chụp ảnh và nhóm sẽ sử dụng mô hình PaliGemma, một mô hình vision-language của Google, để thực hiện image captioning (mô tả bức ảnh một cách ngắn gọn) và hỗ trợ hỏi-đáp dựa trên nội dung hình ảnh đầu vào. Input mô hình sẽ là bức ảnh và câu prompt của người dùng (hay còn nói cách khác là câu hỏi, khi người dùng hỏi thì câu prompt có thể sẽ là “Describe the picture”). Tiếp đến, người dùng có thể tiếp tục hỏi về môi trường xung quanh. nhóm sẽ sử dụng mô hình đã được train thông qua API của Hugging Face, đồng thời sử dụng 2 module text to speech để chuyển câu trả lời text của mô hình sang âm thanh, speech to text để chuyển câu hỏi người dùng sang text cho input của mô hình.

- Chức năng 2: cảnh báo người dùng về các vật cản nguy hiểm trên đường đi nhằm tăng cường an toàn trong quá trình di chuyển. nhóm sử dụng mô hình đã được huấn luyện sẵn MobileNet để phát hiện các vật nguy hiểm trên đường, như ổ chuột, xe ô tô và xe máy tiến gần đến người dùng. MobileNet được chọn vì có khả năng phát hiện vật thể nhanh, độ chính xác cao, kích thước nhỏ gọn và tốc độ suy luận nhanh, rất phù hợp cho các thiết bị di động.

- Chức năng 3: khác chức năng 2 chỉ tập trung vào các vật thể nguy hiểm, chức năng 3 mở rộng phạm vi nhận diện ra toàn bộ các đồ vật có thể thấy, dựa trên danh sách nhãn đã được huấn luyện trong mô hình. Quan trọng hơn, chức năng 3 còn có thể mô tả cho người dùng được vị trí của các đồ vật (thay vì mỗi đọc tên thì hệ thống sẽ còn phát ra âm thanh mô tả như “bottle on the right”, “chair in the front”). Sau khi xác định được các đồ vật xung quanh, ứng dụng sẽ sử dụng mô-đun chuyển văn bản thành giọng nói (text-to-speech) để đọc tên từng vật thể, giúp người dùng có thể hình dung và nắm bắt được thông tin chi tiết về không gian xung quanh họ.
