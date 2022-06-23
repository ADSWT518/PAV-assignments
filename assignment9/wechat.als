// Model discription

sig Post {
    poster: User,               // 发帖人
    comments: set Comment,      // 评论
    post_see_users: set User    // 可以看到这条帖子的用户
} {
    post_see_users = poster.moment_friends  // 可以看到这条帖子的用户是发帖人的「朋友圈可见的好友」
}

sig Comment {
    from_user, to_user: User,   // 评论人和被评论人
    comment_see_users: set User // 可以看到这条评论的用户
} {
    comment_see_users = (from_user.friends - from_user.blacklist) & (to_user.friends - to_user.blacklist) // 可以看到这条评论的用户是评论人的「全部好友」减去发帖人的「黑名单」与被评论人的「全部好友」减去被评论人的「黑名单」的交集
}

sig User {
    friends: set User,          // 全部好友
    moment_friends: set User,   // 朋友圈可见的好友
    other_friends: set User,    // 朋友圈不可见的好友
    blacklist: set User,        // 黑名单
    posts: set Post,            // 朋友圈中发布的帖子
    chat_list: set User,        // 聊天列表
    strangers: set User,        // 陌生人（除了好友以外的其它所有用户）
} {
    friends = moment_friends + other_friends    // 「全部好友」是「朋友圈可见的好友」加上「朋友圈不可见的好友」
    chat_list = friends - blacklist             // 「聊天列表」是「全部好友」减去「黑名单」
}

fact {
    all u: User, p: Post | p in u.posts <=> u = p.poster
    all u: User | u in u.moment_friends
    all u, u1: User | u in u1.friends <=> u1 in u.friends
    all u, u1: User | u in u1.friends <=> not u in u1.strangers
    no u: User | u in u.blacklist
}

pred addToBlacklist(u, u", u1: User) {
    u1 in u.friends
    u != u1
    not u1 in u.blacklist
    u".blacklist = u.blacklist + {u1}
    u".moment_friends = u.moment_friends - {u1}
    u".friends = u.friends
}

pred removeFromBlacklist(u, u", u1: User) {
    u1 in u.friends
    u1 in u.blacklist
    u != u1
    u".blacklist = u.blacklist - {u1}
    u".friends = u.friends
}

// Assertion

addToBlacklistNoChat: check {
    all u, u", u1: User |
        addToBlacklist[u, u", u1] => u".chat_list = u.chat_list - {u1}
} for 50

removeFromBlacklistNoChat: check {
    all u, u", u1: User |
        removeFromBlacklist[u, u", u1] => u".chat_list = u.chat_list + {u1}
} for 50

addToBlacklistNoMoment: check {
    all u, u", u1: User |
        addToBlacklist[u, u", u1] => not u1 in u".moment_friends
} for 50

// Example

pred example {
    all u: User | #u.friends > 3 and #u.blacklist > 3
}

run example for exactly 5 User, 10 Post, 20 Comment