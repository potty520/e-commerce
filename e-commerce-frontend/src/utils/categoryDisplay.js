/** 英文分类名 → 中文展示名（与后端种子/schema 英文分类对齐） */
export const CATEGORY_EN_TO_ZH = {
  Electronics: '数码家电',
  Clothing: '服饰鞋包',
  Books: '图书文娱',
  'Home & Garden': '家居生活',
  Sports: '运动户外',
  Food: '食品生鲜'
}

export function toChineseCategoryName(raw) {
  if (!raw) return '其他'
  const t = String(raw).trim()
  if (CATEGORY_EN_TO_ZH[t]) return CATEGORY_EN_TO_ZH[t]
  return t
}

/**
 * 侧边栏/首页：映射为中文后按中文名去重，同一类保留较小 id（与接口筛选一致）
 * @returns {{ id: number, displayName: string }[]}
 */
export function mergeCategoriesForMenu(categories) {
  if (!categories?.length) return []
  const byZh = new Map()
  for (const cat of categories) {
    const zh = toChineseCategoryName(cat.name)
    const prev = byZh.get(zh)
    if (!prev || Number(cat.id) < Number(prev.id)) {
      byZh.set(zh, { id: cat.id, displayName: zh })
    }
  }
  return Array.from(byZh.values()).sort((a, b) => a.id - b.id)
}
