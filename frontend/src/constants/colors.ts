export const colors = {
  두산: "#1A1748",
  롯데: "#041E42",
  키움: "#570514",
  KIA: "#EA0029",
  LG: "#C30452",
  한화: "#FC4E00",
  SSG: "#CE0E2D",
  삼성: "#074CA1",
  NC: "#315288",
  KT: "#000000"
} as const;

export type TeamColor = keyof typeof colors;