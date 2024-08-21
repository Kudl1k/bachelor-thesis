import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination"
   
interface PaggingMenuProps {
    totalPosts: number;
    postsPerPage: number;
    currentPage: number;
    setCurrentPage: (page: number) => void;
}


export function PaggingMenu({totalPosts, postsPerPage, currentPage, setCurrentPage}: PaggingMenuProps) {
    const pages: number[] = [];

    for (let i = 1; i <= Math.ceil(totalPosts / postsPerPage); i++) {
        pages.push(i);
    }
    console.log(pages);

    const handlePageClick = (page: number) => {
        if (page < 1 || page > pages.length) {
            return;
        }
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });

        const params = new URLSearchParams(window.location.search);
        params.set('page', page.toString());
        window.history.replaceState({}, '', `${window.location.pathname}?${params.toString()}`);
    };

    return (
        <div className="pb-3">
            <Pagination>
                
                <PaginationContent>
                    
                    <PaginationItem>
                        <PaginationPrevious onClick={() => handlePageClick(currentPage - 1)} />
                    </PaginationItem>

                    {pages.map((page) => (
                        <PaginationItem key={page}>
                        <PaginationLink onClick={() => handlePageClick(page)} isActive={currentPage === page}>{page}</PaginationLink>
                        </PaginationItem>
                    ))}
                    <PaginationItem>
                        <PaginationNext onClick={() => handlePageClick(currentPage+1)} />
                    </PaginationItem>
                    
                </PaginationContent>
                
            </Pagination>
        </div>
    )
}