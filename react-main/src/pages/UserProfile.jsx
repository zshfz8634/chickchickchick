import style from "../styles/UserProfile.module.scss";
import { Table, Modal, Button } from "react-bootstrap";
import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { Context } from "../context/Context";
import 정렬 from "../images/정렬.png";
import 기본프로필 from "../images/프로필.png";

const UserProfile = () => {
  const { currentUser, setCurrentUser } = useContext(Context);
  const navigate = useNavigate();
  const { id } = useParams();
  const [userInfo, setUserInfo] = useState(null);
  const [nicknameChangeInputShow, setNicknameChangeInputShow] = useState(false);
  const [nickname, setNickname] = useState("");
  const [showMessageModal, setShowMessageModal] = useState(false);
  const [message, setMessage] = useState("");
  const [showProfileImageModal, setShowProfileImageModal] = useState(false);
  const [profileImage, setProfileImage] = useState(null);

  const handleChangeNickname = async () => {
    try {
      await axios.post(
        "http://localhost:8080/profile/updateNickname",
        { newNickname: nickname },
        {
          withCredentials: true,
        }
      );
      setUserInfo({
        ...userInfo,
        member: { ...userInfo.member, nickname: nickname },
      });
      setCurrentUser({ ...currentUser, nickname: nickname });
      setNicknameChangeInputShow(false);
    } catch (error) {
      console.log(error);
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    try {
      await axios.post(
        `http://localhost:8080/messages/send/${id}`,
        {
          content: message,
        },
        {
          withCredentials: true,
        }
      );
      alert("메세지가 전송되었습니다.");
      setMessage("");
    } catch (error) {
      alert(error.response.data);
    }
  };

  const handleProfileImageChange = (e) => {
    setProfileImage(e.target.files[0]);
  };

  const handleUploadProfileImage = async () => {
    const formData = new FormData();
    formData.append("file", profileImage);

    try {
      const res = await axios.post(
        "http://localhost:8080/profile/updateImage",
        formData,
        {
          withCredentials: true,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setUserInfo({
        ...userInfo,
        member: { ...userInfo.member, profileImageUrl: res.data },
      });
      setShowProfileImageModal(false);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    const getUserInfo = async () => {
      if (id === String(currentUser.id)) {
        try {
          const res = await axios.get("http://localhost:8080/profile", {
            withCredentials: true,
          });
          setUserInfo(res.data);
        } catch (error) {
          console.log(error);
        }
      } else {
        try {
          const res = await axios.get(
            `http://localhost:8080/members/${id}/profile`
          );
          setUserInfo(res.data);
        } catch (error) {
          console.log(error);
        }
      }
    };

    getUserInfo();
  }, [id, currentUser]);

  if (!userInfo) {
    return <div>Loading...</div>;
  }

  return (
    <div className={style.container}>
      <div className={style.userInfoContainer}>
        <div className={style.image}>
          <img
            src={
              userInfo.member.profileImageUrl
                ? `http://localhost:8080${userInfo.member.profileImageUrl}`
                : 기본프로필
            }
            alt="프로필 이미지"
          />
        </div>
        <div className={style.userInfo}>
          <span>이름: {userInfo.member.name}</span>
          <span>이메일: {userInfo.member.email}</span>
          {nicknameChangeInputShow ? (
            <div className={style.nicknameChangeContainer}>
              <input
                type="text"
                placeholder="닉네임"
                value={nickname}
                onChange={(e) => {
                  setNickname(e.target.value);
                }}
              />
              <button onClick={handleChangeNickname}>변경</button>
              <button
                onClick={() => {
                  setNicknameChangeInputShow(false);
                }}
              >
                취소
              </button>
            </div>
          ) : (
            <span>닉네임: {userInfo.member.nickname}</span>
          )}

          <div className={style.buttonGroup}>
            {id === String(currentUser.id) ? (
              <>
                <button
                  onClick={() => {
                    setNicknameChangeInputShow(true);
                  }}
                >
                  닉네임 변경
                </button>
                <button onClick={() => setShowProfileImageModal(true)}>
                  프로필 이미지 변경
                </button>
              </>
            ) : (
              <button
                onClick={() => {
                  setShowMessageModal(true);
                }}
              >
                메세지 보내기
              </button>
            )}
          </div>
        </div>
      </div>
      <div className={style.userBoard}>
        <div className={style.header}>
          <span>최근 작성한 게시물</span>
        </div>
        <div className={style.body}>
          <Table hover>
            <thead>
              <tr>
                <th>제목</th>
                <th>작성일</th>
                <th>
                  댓글 <img src={정렬} alt="" />
                </th>
                <th>
                  조회수 <img src={정렬} alt="" />
                </th>
                <th>
                  좋아요 <img src={정렬} alt="" />
                </th>
              </tr>
            </thead>
            <tbody>
              {userInfo.posts.map((item, index) => (
                <tr
                  key={index}
                  onClick={() => {
                    navigate(
                      `/single/${
                        item.employmentType === "EMPLOYEE" ? "알바" : "사장"
                      }${item.brand.name}/${item.id}`
                    );
                  }}
                >
                  <td>{item.title}</td>
                  <td>{item.createdAt.replace("T", " ")}</td>
                  <td>{item.comments.length}</td>
                  <td>{item.viewCount}</td>
                  <td>{item.likeCount}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </div>
      <Modal show={showMessageModal} onHide={() => setShowMessageModal(false)}>
        <Modal.Header>
          <Modal.Title className={style.modalTitle}>메세지 보내기</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <textarea
            className={style.textarea}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
          />
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowMessageModal(false)}
          >
            닫기
          </Button>
          <Button variant="primary" onClick={handleSendMessage}>
            보내기
          </Button>
        </Modal.Footer>
      </Modal>
      <Modal
        show={showProfileImageModal}
        onHide={() => setShowProfileImageModal(false)}
      >
        <Modal.Header>
          <Modal.Title className={style.modalTitle}>
            프로필 이미지 변경
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <input
            type="file"
            accept="image/*"
            onChange={handleProfileImageChange}
          />
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="secondary"
            onClick={() => setShowProfileImageModal(false)}
          >
            닫기
          </Button>
          <Button variant="primary" onClick={handleUploadProfileImage}>
            업로드
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default UserProfile;
