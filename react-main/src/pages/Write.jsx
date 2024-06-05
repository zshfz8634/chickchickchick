import style from "../styles/Write.module.scss";
import { useContext, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { Context } from "../context/Context";

const Write = () => {
  const { currentUser } = useContext(Context);
  const navigate = useNavigate();
  const { id } = useParams();
  const location = useLocation();
  const singleUpdatePost = location.state?.single || {};
  const [title, setTitle] = useState(singleUpdatePost.title || "");
  const [content, setContent] = useState(singleUpdatePost.content || "");
  const [images, setImages] = useState([]);

  const handleImageChange = (e) => {
    setImages(e.target.files);
  };

  const handleSubmit = async () => {
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    for (let i = 0; i < images.length; i++) {
      formData.append("images", images[i]);
    }

    try {
      if (singleUpdatePost.id) {
        await axios.put(
          `http://localhost:8080/posts/${singleUpdatePost.id}`,
          formData,
          {
            withCredentials: true,
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        alert("게시글이 수정되었습니다.");
        navigate(-1);
      } else {
        await axios.post(
          `http://localhost:8080/brands/${id.slice(2)}/posts/${
            currentUser.employmentType === "EMPLOYEE" ? "employee" : "boss"
          }/new`,
          formData,
          {
            withCredentials: true,
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        alert("게시글이 등록되었습니다.");
        navigate(`/board/${id}`);
      }
    } catch (error) {
      console.error(error);
      if (error.response && error.response.data) {
        alert(error.response.data);
      } else {
        alert("Something went wrong. Please try again.");
      }
    }
  };

  return (
    <div className={style.container}>
      <input
        required
        type="text"
        value={title}
        placeholder="제목"
        onChange={(e) => {
          setTitle(e.target.value);
        }}
      />
      <textarea
        required
        value={content}
        placeholder="내용"
        onChange={(e) => {
          setContent(e.target.value);
        }}
      />
      <input
        type="file"
        multiple
        onChange={handleImageChange}
      />
      <div className={style.buttonGroup}>
        {singleUpdatePost.id ? (
          <button onClick={handleSubmit}>수정</button>
        ) : (
          <button onClick={handleSubmit}>등록</button>
        )}
      </div>
    </div>
  );
};

export default Write;
