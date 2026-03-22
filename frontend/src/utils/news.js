import defaultNewsCover from '../assets/default-cover.jpg';

export const defaultNewsImage = defaultNewsCover;

export const extractFirstImageFromContent = (content) => {
  if (!content || typeof content !== 'string') return '';
  const match = content.match(/<img\b[^>]*\bsrc=['"]([^'"]+)['"][^>]*>/i);
  return match?.[1] || '';
};

export const getNewsDisplayCover = (news) => {
  const contentCover = extractFirstImageFromContent(news?.content);
  return contentCover || news?.cover || news?.coverUrl || news?.image || defaultNewsImage;
};

export const normalizeNewsCard = (news) => ({
  ...news,
  displayCover: getNewsDisplayCover(news)
});
