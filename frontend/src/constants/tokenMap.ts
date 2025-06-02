export const teamToTokenIdMap: { [key: string]: number } = {
    키움: 6,
    두산: 2,
    롯데: 5,
    삼성: 10,
    한화: 11,
    KIA: 9,
    LG: 4,
    SSG: 3,
    NC: 7,
    KT: 8,
};

// 토큰 ID를 기준으로 팀 정보를 매핑
export const tokenIdToTeamMap: { [key: number]: string } = {
    6: '키움',
    2: '두산',
    5: '롯데',
    10: '삼성',
    11: '한화',
    9: 'KIA',
    4: 'LG',
    3: 'SSG',
    7: 'NC',
    8: 'KT'
};